/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.analysis.cfa.traverseForwardWithoutLoops
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ControlFlowGraph
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionEnterNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.PropertyInitializerEnterNode
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.name.ClassId

open class ClassInitContext(val classDeclaration: FirRegularClass) {

    val anonymousFunctionsContext = mutableMapOf<FunctionEnterNode, MutableMap<CFGNode<*>, InitContextNode>>()
    val initContextNodes = mutableMapOf<CFGNode<*>, InitContextNode>()
    private val primaryConstructorParams = mutableListOf<FirVariableSymbol<*>>()
    private val gettersContext = mutableMapOf<FirVariableSymbol<*>, MutableMap<CFGNode<*>, InitContextNode>>()
    private val settersContext = mutableMapOf<FirVariableSymbol<*>, MutableMap<CFGNode<*>, InitContextNode>>()

    val classId: ClassId
        get() = classDeclaration.symbol.classId

    val isCfgAvailable: Boolean
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph != null

    val classCfg: ControlFlowGraph
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph!!

    val isDerivedClassAndOverridesFun: Boolean
        get() = (this as? DerivedClassInitContext)?.overrideFunctions?.isNotEmpty() ?: false


    init {
        if (isCfgAvailable) {
            // to detect initialization by constructor parameters
            for (declaration in classDeclaration.declarations) {
                if (declaration is FirConstructor)
                    primaryConstructorParams.addAll(declaration.valueParameters.map { it.symbol })
            }

            // collect lambdas and property getter/setter data
            for (graph in classCfg.subGraphs) {
                for (subGraph in graph.subGraphs) {
                    if (subGraph.enterNode is FunctionEnterNode)
                        when (val function = subGraph.enterNode.fir) {
                            is FirAnonymousFunction -> {
                                val visitor = ForwardCfgVisitor(
                                    classDeclaration.symbol.classId,
                                    mutableMapOf()
                                )
                                subGraph.traverseForwardWithoutLoops(visitor, this)
                                anonymousFunctionsContext[subGraph.enterNode as FunctionEnterNode] = visitor.initContextNodes
                            }
                            is FirPropertyAccessor -> {
                                val graphEnterNode = graph.enterNode
                                if (graphEnterNode is PropertyInitializerEnterNode) {
                                    val visitor = ForwardCfgVisitor(
                                        classDeclaration.symbol.classId,
                                        mutableMapOf()
                                    )
                                    subGraph.traverseForwardWithoutLoops(visitor, this)
                                    if (function.isGetter)
                                        gettersContext[graphEnterNode.fir.symbol] = visitor.initContextNodes
                                    else
                                        settersContext[graphEnterNode.fir.symbol] = visitor.initContextNodes
                                }
                            }
                            else -> {
                            }
                        }
                }

            }

            // 1st traverse class cfg for context nodes collecting
            val visitor = ForwardCfgVisitor(classDeclaration.symbol.classId, anonymousFunctionsContext, primaryConstructorParams)
            classCfg.traverseForwardWithoutLoops(visitor, this)
            initContextNodes.putAll(visitor.initContextNodes)

        }

    }


}
