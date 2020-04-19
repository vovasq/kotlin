/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.analysis.cfa.TraverseDirection
import org.jetbrains.kotlin.fir.analysis.cfa.traverse
import org.jetbrains.kotlin.fir.declarations.FirAnonymousInitializer
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.*
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol


// TODO: rename class:))
internal class ClassWithOutSuperDeclarationContext(override val classDeclaration: FirRegularClass) : ClassDeclarationContext() {
    override val propsDeclList = mutableListOf<FirProperty>()
    override val anonymousInitializer = mutableListOf<FirAnonymousInitializer>()
    override val isClassNotRelevantForChecker: Boolean
        get() = propsDeclList.isEmpty()

    override val classInitStates = mutableListOf<ClassInitStateForNode>()

    val initializedProps = mutableListOf<FirVariableSymbol<*>>()

    init {
        if (isCfgAvailable)
            for (subGraph in classCfg.subGraphs) {
                when (val declaration = subGraph.declaration) {
                    is FirAnonymousInitializer -> anonymousInitializer.add(declaration)
                    is FirProperty -> propsDeclList.add(declaration)
                }
            }
        val visitor = ClassControlFlowGraphVisitor(classDeclaration)
        classCfg.traverse(TraverseDirection.Forward, visitor)
        classInitStates.addAll(visitor.initStates)
        initializedProps.addAll(visitor.initializedProps)
    }


    private class ClassControlFlowGraphVisitor(
        private val classDeclaration: FirRegularClass,
    ) : ControlFlowGraphVisitorVoid() {

        val initStates = mutableListOf<ClassInitStateForNode>()
        val initializedProps = mutableListOf<FirPropertySymbol>()
        val possibleWeakNodes = mutableListOf<CFGNode<*>>()

        override fun visitNode(node: CFGNode<*>) {
            initStates.add(
                ClassInitStateForNode(
                    node,
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    InitState.NOT_AFFECTED
                )
            )
        }

        override fun visitFunctionCallNode(node: FunctionCallNode) {
            val accessedMembers = mutableListOf<AbstractFirBasedSymbol<*>>()
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
            if (node.fir.calleeReference.isCalleeReferenceMemberOfTheClass(classDeclaration.symbol.classId))
                accessedMembers.add(node.fir.calleeReference.resolvedSymbolAsNamedFunction!!)
            for (argument in node.fir.argumentList.arguments) {
                if ((argument is FirQualifiedAccessExpression) && argument.calleeReference.isCalleeReferenceMemberOfTheClass(
                        classDeclaration.symbol.classId
                    )
                ) {
                    accessedProperties.add(argument.calleeReference.resolvedSymbolAsProperty!!)
                    accessedMembers.add(argument.calleeReference.resolvedSymbolAsProperty!!)
                }
            }
            val initState = if (!accessedProperties.all { it in initializedProps }) InitState.INIT_FAIL else InitState.POSSIBLE_WEAK
            if (initState == InitState.POSSIBLE_WEAK)
                possibleWeakNodes.add(node)
            initStates.add(
                ClassInitStateForNode(node, accessedMembers, accessedProperties, emptyList(), initState)
            )
        }

        override fun visitQualifiedAccessNode(node: QualifiedAccessNode) {
            val accessedMembers = mutableListOf<AbstractFirBasedSymbol<*>>()
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
            if (node.fir.calleeReference.isCalleeReferenceMemberOfTheClass(classDeclaration.symbol.classId)) {
                accessedMembers.add(node.fir.calleeReference.resolvedSymbolAsProperty!!)
            }
            val initState = if (!accessedProperties.all { it in initializedProps }) InitState.INIT_FAIL else InitState.INIT_OK
            initStates.add(
                ClassInitStateForNode(node, accessedMembers, accessedProperties, emptyList(), initState)
            )
        }

        override fun visitVariableAssignmentNode(node: VariableAssignmentNode) {
            val accessedMembers = mutableListOf<AbstractFirBasedSymbol<*>>()
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>()

            if (node.fir.lValue.resolvedSymbolAsProperty?.callableId?.classId == classDeclaration.symbol.classId) {
                val symbol = node.fir.lValue.resolvedSymbolAsProperty
                accessedMembers.add(symbol!!)
                accessedProperties.add(symbol)
                initializedProps.add(symbol)
                initStates.add(
                    ClassInitStateForNode(
                        node,
                        accessedMembers,
                        accessedProperties,
                        mutableListOf(symbol),
                        InitState.INIT_OK
                    )
                )
            } else
                visitNode(node)

        }
    }

}
