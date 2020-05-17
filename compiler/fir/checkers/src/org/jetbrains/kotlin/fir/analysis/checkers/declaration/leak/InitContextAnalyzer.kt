/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.FirSourceElement
import org.jetbrains.kotlin.fir.analysis.cfa.traverseForwardWithoutLoops
import org.jetbrains.kotlin.fir.analysis.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ConstExpressionNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ControlFlowGraph
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionCallNode
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.name.ClassId


internal class InitContextAnalyzer(
    private val classInitContext: BaseClassInitContext,
    private val reporter: DiagnosticReporter,
    private val maxResolvedCallLevel: Int
) {

    private val initializedProperties = mutableSetOf<FirVariableSymbol<*>>()
    private val reportedProperties = mutableSetOf<FirVariableSymbol<*>>()

    private var callLevel: Int = 0
    private val resolvedCalls = mutableSetOf<FirCallableSymbol<*>>()

    private val classId: ClassId
        get() = classInitContext.classId

    fun analyze() {
        if (classInitContext.isCfgAvailable)
            classInitContext.classCfg.traverseForwardWithoutLoops(ForwardCfgVisitor(classInitContext.classId), analyze = this::analyze)
    }

    private fun analyze(node: CFGNode<*>) {
        val contextNode = classInitContext.classInitContextNodesMap[node] ?: return
        when (contextNode.nodeType) {
            ContextNodeType.ASSIGNMENT_OR_INITIALIZER -> {
                if (contextNode.isSuccessfullyInitNode()) {
                    contextNode.confirmInitForCandidate()
                    initializedProperties.add(contextNode.initCandidate)
                }
            }
            ContextNodeType.PROPERTY_QUALIFIED_ACCESS -> {
                if (contextNode.accessedProperties.any {
                        it !in initializedProperties && it !in reportedProperties
                    }
                ) {
                    reporter.report(contextNode.cfgNode.fir.source)
                    reportedProperties.add(contextNode.firstAccessedProperty)
                }
            }
            ContextNodeType.RESOLVABLE_THIS_RECEIVER_CALL, ContextNodeType.RESOLVABLE_CALL -> {
                if (callLevel < maxResolvedCallLevel) {
                    callLevel += 1
                    resolveCall(contextNode)
                }
            }
            else -> return
        }

    }

    private fun resolveCall(contextNode: InitContextNode) {
//        try {
        val callableCfg = contextNode.callableCFG ?: return
        val callableBodyVisitor = ForwardCfgVisitor(classId)
        if (resolvedCalls.add(contextNode.callableSymbol ?: return)) {
            callableCfg.traverseForwardWithoutLoops(callableBodyVisitor)
            classInitContext.classInitContextNodesMap.putAll(callableBodyVisitor.initContextNodesMap)
        }
        callableCfg.traverseForwardWithoutLoops(callableBodyVisitor, analyze = this::analyze)
//        } catch (e: Exception) {
//        }
    }

    private fun DiagnosticReporter.report(source: FirSourceElement?) {
        source?.let { report(FirErrors.LEAKING_THIS_IN_CONSTRUCTOR.on(it, "Possible leaking this in constructor")) }
    }

    private fun InitContextNode.isSuccessfullyInitNode(): Boolean =
        affectingNodes.all {
            it.cfgNode is ConstExpressionNode
                    || (it.nodeType == ContextNodeType.UNRESOLVABLE_FUN_CALL)
                    || (it.nodeType == ContextNodeType.PROPERTY_QUALIFIED_ACCESS
                    && it.firstAccessedProperty.callableId.classId == classId
                    && initializedProperties.contains(it.firstAccessedProperty))
        }

    private val InitContextNode.callableCFG: ControlFlowGraph?
        get() = (cfgNode as FunctionCallNode).fir.calleeReference.resolvedSymbolAsNamedFunction?.fir?.controlFlowGraphReference?.controlFlowGraph

    private val InitContextNode.callableSymbol: FirCallableSymbol<*>?
        get() = (cfgNode as FunctionCallNode).fir.calleeReference.resolvedSymbolAsNamedFunction


}