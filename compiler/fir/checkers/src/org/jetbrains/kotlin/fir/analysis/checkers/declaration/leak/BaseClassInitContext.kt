/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.analysis.cfa.traverseForwardWithoutLoops
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionEnterNode
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol

internal class BaseClassInitContext(override val classDeclaration: FirRegularClass) : ClassInitContext() {

    override val classAnonymousFunctions = mutableMapOf<FunctionEnterNode, MutableMap<CFGNode<*>, InitContextNode>>()
    override val primaryConstructorParams = mutableListOf<FirVariableSymbol<*>>()
    override val classInitContextNodesMap = mutableMapOf<CFGNode<*>, InitContextNode>()

    init {
        if (isCfgAvailable) {
            for (declaration in classDeclaration.declarations) {
                if (declaration is FirConstructor)
                    primaryConstructorParams.addAll(declaration.valueParameters.map { it.symbol })
            }

            // collect lambda/property data
            for (graph in classCfg.subGraphs) {
                for (subGraph in graph.subGraphs) {
                    if (subGraph.enterNode is FunctionEnterNode && subGraph.enterNode.fir is FirAnonymousFunction) {
                        val visitor = ForwardCfgVisitor(
                            classDeclaration.symbol.classId,
                            mutableMapOf()
                        )
                        subGraph.traverseForwardWithoutLoops(visitor,this)
                        classAnonymousFunctions[subGraph.enterNode as FunctionEnterNode] = visitor.initContextNodesMap
                    }
                }
            }

            // 1st traverse class cfg for context nodes collecting
            val visitor = ForwardCfgVisitor(classDeclaration.symbol.classId, classAnonymousFunctions, primaryConstructorParams)
            classCfg.traverseForwardWithoutLoops(visitor, this)
            classInitContextNodesMap.putAll(visitor.initContextNodesMap)
        }

    }
}
