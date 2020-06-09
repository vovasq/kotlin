/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.FirSourceElement
import org.jetbrains.kotlin.fir.analysis.cfa.traverseForwardWithoutLoops
import org.jetbrains.kotlin.fir.analysis.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.*
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name


@OptIn(ExperimentalStdlibApi::class)
internal class InitContextAnalyzer(
    private val session: FirSession,
    private val initContext: ClassInitContext,
    private val reporter: DiagnosticReporter,
    private val maxCallResolved: Int,
    private val maxSuperTypesResolved: Int
) {

    private val initializedProperties = mutableSetOf<FirVariableSymbol<*>>()
    private val reportedProperties = mutableSetOf<FirVariableSymbol<*>>()

    private var callResolved: Int = 0
    private var superTypesResolved: Int = 0

    private val resolvedCalls = mutableSetOf<FirCallableSymbol<*>>()

    private val classId: ClassId
        get() = initContext.classId


    fun analyze() {
        if (initContext.isCfgAvailable)
            if (initContext.isDerivedClassAndOverridesFun)
                runSuperTypesAnalysis()
            else
                runContextAnalysis(initContext)
    }

    private fun runSuperTypesAnalysis()
    {

        for (typeRef in initContext.classDeclaration.superTypeRefs) {
            if (superTypesResolved < maxSuperTypesResolved) {
                val superClassDeclaration = typeRef.getFirSuperTypeRegularClass(session) ?: continue
                val superClassInitContext = createClassInitContext(superClassDeclaration)
                initContext.superTypesInitContexts?.addFirst(superClassInitContext)
                superTypesResolved++
            }
        }

        for (superTypeContext in initContext.superTypesInitContexts!!) {
            runContextAnalysis(superTypeContext)
//            updateMainInitContext()
        }

    }
    private fun runSuperTypesAnalysisWithRecurs() {
        val stack = ArrayDeque<FirRegularClass>()
        for (typeRef in initContext.classDeclaration.superTypeRefs) {
            if (superTypesResolved < maxSuperTypesResolved) {
                stack.addFirst(typeRef.getFirSuperTypeRegularClass(session) ?: continue)
                superTypesResolved++
            }
        }
        while (stack.isNotEmpty()) {
            val superClassDeclaration = stack.removeFirst()
            val superClassInitContext = createClassInitContext(superClassDeclaration)
            if (!superClassInitContext.isDerivedClassAndOverridesFun || superTypesResolved < maxSuperTypesResolved)
                break
            initContext.superTypesInitContexts?.addFirst(superClassInitContext)
            for (typeRef in superClassDeclaration.superTypeRefs) {
                if (superTypesResolved < maxSuperTypesResolved) {
                    stack.addFirst(typeRef.getFirSuperTypeRegularClass(session) ?: continue)
                    superTypesResolved++
                }
            }
        }
//        val functions = initContext.overrideFunctions!!.map { it.value.name }.toList()
//        println(functions)

        for (superTypeContext in initContext.superTypesInitContexts!!) {
            runContextAnalysis(superTypeContext)
            updateMainInitContext(superTypeContext)
        }
    }

    private fun updateMainInitContext(curInitContext: ClassInitContext) {
        initContext.anonymousFunctionsContext.putAll(curInitContext.anonymousFunctionsContext)
        initContext.initContextNodes.putAll(curInitContext.initContextNodes)
        if (initContext.overrideFunctions != null && curInitContext.overrideFunctions != null)
            for (pair in curInitContext.overrideFunctions!!) {
                initContext.overrideFunctions!!.putIfAbsent(pair.key, pair.value)
            }
    }

    private fun runContextAnalysis(curInitContext: ClassInitContext) {
        if (curInitContext.isCfgAvailable)
            curInitContext.classCfg.traverseForwardWithoutLoops(
                LightCfgVisitor(),
                curInitContext,
                analyze = this::analyze
            )
    }

    internal fun analyze(node: CFGNode<*>, curInitContext: ClassInitContext) {
        val contextNode = curInitContext.initContextNodes[node] ?: return
        when (contextNode.nodeType) {
            ContextNodeType.ASSIGNMENT_OR_INITIALIZER -> {
                if (contextNode.isSuccessfullyInitNode()) {
                    contextNode.confirmInitForCandidate()
                    initializedProperties.add(contextNode.initCandidate)
                }
            }
            ContextNodeType.PROPERTY_QUALIFIED_ACCESS -> {
                contextNode.checkIfPropertyAccessOk()
            }
            ContextNodeType.NOT_MEMBER_QUALIFIED_ACCESS -> {
                if (contextNode.isPropertyOfSuperType())
                    contextNode.checkIfPropertyAccessOk()
            }
            ContextNodeType.RESOLVABLE_THIS_RECEIVER_CALL, ContextNodeType.RESOLVABLE_CALL -> {
                if (callResolved < maxCallResolved) {
                    callResolved += 1
                    resolveCallOrPropertyGetter(contextNode, curInitContext)
                }
            }
            else -> return
        }
    }

    private fun resolveCallOrPropertyGetter(contextNode: InitContextNode, curInitContext: ClassInitContext) {
//        try { TODO: uncomment after all tests

        val overrideCall = initContext.overrideFunctions?.get(contextNode.callableName)
        var callableCfg = contextNode.callableCFG ?: return
        var callableSymbol = contextNode.callableSymbol ?: return

        if (overrideCall != null) {
            callableCfg = overrideCall.controlFlowGraphReference.controlFlowGraph ?: return
            callableSymbol = overrideCall.symbol
        }

        if (resolvedCalls.add(callableSymbol)) {
            val callableBodyVisitor = ForwardCfgVisitor(classId, curInitContext.anonymousFunctionsContext)
            callableCfg.traverseForwardWithoutLoops(callableBodyVisitor, curInitContext)
            curInitContext.initContextNodes.putAll(callableBodyVisitor.initContextNodes)
            contextNode.affectingNodes.addAll(callableBodyVisitor.initContextNodes.values)
        }
        callableCfg.traverseForwardWithoutLoops(LightCfgVisitor(), curInitContext, analyze = this::analyze)
        if (contextNode.nodeType == ContextNodeType.PROPERTY_QUALIFIED_ACCESS) {
            if (contextNode.isSuccessfullyInitNode()) {
                contextNode.confirmInitForCandidate()
                initializedProperties.add(contextNode.firstAccessedProperty)
            }
        }
        //        } catch (e: Exception) {
//        }
    }

    private fun DiagnosticReporter.report(source: FirSourceElement?) {
        source?.let { report(FirErrors.LEAKING_THIS_IN_CONSTRUCTOR.on(it, "Possible leaking this in constructor")) }
    }

    private val FirPropertyAccessor.isNotEmpty: Boolean
        get() = (this.controlFlowGraphReference.controlFlowGraph?.nodes?.size ?: 0) > 2

    private val FirPropertyAccessor.graph: ControlFlowGraph?
        get() = this.controlFlowGraphReference.controlFlowGraph!!

    private fun InitContextNode.checkIfPropertyAccessOk(): Boolean {
        fun report(): Boolean {
            reporter.report(cfgNode.fir.source)
            reportedProperties.add(firstAccessedProperty)
            return false
        }
        if (firstAccessedProperty.fir.getter != null && firstAccessedProperty.fir.getter!!.isNotEmpty
            && firstAccessedProperty !in initializedProperties && firstAccessedProperty !in reportedProperties
        ) {
            resolveCallOrPropertyGetter(this, initContext)
        }
        if (isPropertyOfSuperType()) {
            if (firstAccessedProperty.callableId.callableName.asString() !in initializedProperties.map { it.callableId.callableName.asString() }
                && firstAccessedProperty.callableId.callableName.asString() !in reportedProperties.map { it.callableId.callableName.asString() }
            ) {
                println("super in report: ${firstAccessedProperty.callableId.callableName} inited props: ${initializedProperties.map { it.callableId.callableName }}, reported: ${reportedProperties.map { it.callableId.callableName }}")
                return report()
            }
        } else if (firstAccessedProperty !in initializedProperties && firstAccessedProperty !in reportedProperties) {
            println("in report: ${firstAccessedProperty.callableId.callableName} inited props: ${initializedProperties.map { it.callableId.callableName }}, reported: ${reportedProperties.map { it.callableId.callableName }}")
            return report()
        }
        return true
    }

    private fun InitContextNode.isPropertyOfSuperType(): Boolean {
        if (initContext.isDerivedClassAndOverridesFun)
            for (context in initContext.superTypesInitContexts!!) {
                if (accessedProperties.any { it.callableId.classId == context.classId })
                    return true
            }
        return false
    }

    private fun InitContextNode.isSuccessfullyInitNode(): Boolean =
        affectingNodes.all {
            it.nodeType != ContextNodeType.PROPERTY_QUALIFIED_ACCESS
                    || (it.nodeType == ContextNodeType.PROPERTY_QUALIFIED_ACCESS
                    && it.checkIfPropertyAccessOk()
                    && initializedProperties.contains(it.firstAccessedProperty))
//                    && it.firstAccessedProperty.callableId.classId == classId // not fact
//                    && initializedProperties.contains(it.firstAccessedProperty))
        }

    private val InitContextNode.callableCFG: ControlFlowGraph?
        get() = when (cfgNode) {
            is FunctionCallNode -> cfgNode.fir.calleeReference.resolvedSymbolAsNamedFunction?.fir?.controlFlowGraphReference?.controlFlowGraph
            is QualifiedAccessNode -> firstAccessedProperty.fir.getter?.graph
            else -> null
        }

    private val InitContextNode.callableName: Name?
        get() = (cfgNode as FunctionCallNode).fir.calleeReference.resolvedSymbolAsNamedFunction?.fir?.name

    private val InitContextNode.callableSymbol: FirCallableSymbol<*>?
        get() = when (cfgNode) {
            is FunctionCallNode -> cfgNode.fir.calleeReference.resolvedSymbolAsNamedFunction
            is QualifiedAccessNode -> cfgNode.fir.calleeReference.resolvedSymbolAsProperty
            else -> null
        }

    private val ClassInitContext.superTypesInitContexts: ArrayDeque<ClassInitContext>?
        get() = (this as? DerivedClassInitContext)?.superTypesInitContexts

    private val ClassInitContext.overrideFunctions: MutableMap<Name, FirSimpleFunction>?
        get() = (this as? DerivedClassInitContext)?.overrideFunctions

    private class LightCfgVisitor : ControlFlowGraphVisitorVoid() {
        override fun visitNode(node: CFGNode<*>) {
//            very light :)))
        }
    }

}