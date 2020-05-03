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
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.*
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol


internal class BaseClassMembersContext(val classDeclaration: FirRegularClass) {
    val isClassNotRelevantForChecker: Boolean
        get() = propsDeclList.isEmpty()

    val isCfgAvailable: Boolean
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph != null

    private val classCfg: ControlFlowGraph
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph!!

    val classInitStates = mutableListOf<InitializeContextNode>()

    val initializedProps = mutableListOf<FirVariableSymbol<*>>()

    private val propsDeclList = mutableListOf<FirProperty>()
    private val anonymousInitializer = mutableListOf<FirAnonymousInitializer>()

    init {
        if (isCfgAvailable)
            for (subGraph in classCfg.subGraphs) {
                when (val declaration = subGraph.declaration) {
                    is FirAnonymousInitializer -> anonymousInitializer.add(declaration)
                    is FirProperty -> propsDeclList.add(declaration)
                }
            }
        val visitor = BackwardClassCfgVisitor(classDeclaration)
        classCfg.traverse(TraverseDirection.Backward, visitor)
        classInitStates.addAll(visitor.initStates)
        initializedProps.addAll(visitor.initializedProps)
    }

    private class BackwardClassCfgVisitor(
        private val classDeclaration: FirRegularClass,
    ) : ControlFlowGraphVisitorVoid() {

        val initStates = mutableListOf<InitializeContextNode>()
        val initializedProps = mutableListOf<FirVariableSymbol<*>>()
// TODO
//        val initCandidates = mutableListOf<FirVariableSymbol<*>>()

        var lastAssignmentContextNode: InitializeContextNode? = null
        var lastAssignRValue: FirExpression? = null
        var isInRvalueOfAssignment: Boolean = false
        var lastAssignmentAffectingNodes = mutableListOf<CFGNode<*>>()

        var isInPropertyInitializer = false
        var lastPropertyInitializerAffectingNodes = mutableListOf<CFGNode<*>>()
        var lastPropertyInitializerContextNode: InitializeContextNode? = null

        override fun visitNode(node: CFGNode<*>) {
            initStates.add(checkAndBuildNodeContext(cfgNode = node))
        }

//        TODO
//        override fun visitConstExpressionNode(node: ConstExpressionNode) {
//            visitNode(node)
//        }

        override fun visitFunctionCallNode(node: FunctionCallNode) {
            val accessedMembers = mutableListOf<FirCallableSymbol<*>>()
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
            var initState = InitState.UNRESOLVABLE_FUN_CALL
            if (node.fir.calleeReference.isMemberOfTheClass(classDeclaration.symbol.classId)) {
                accessedMembers.add(node.fir.calleeReference.resolvedSymbolAsNamedFunction!!)
                for (argument in node.fir.argumentList.arguments) {
                    if ((argument is FirQualifiedAccessExpression)
                        && argument.calleeReference.isMemberOfTheClass(classDeclaration.symbol.classId)
                    ) {
//                    TODO: clean up with casting
                        accessedMembers.add(argument.calleeReference.resolvedSymbolAsCallable!!)
                        if (argument.calleeReference.resolvedSymbolAsProperty != null)
                            accessedProperties.add(argument.calleeReference.resolvedSymbolAsProperty!!)
                    }
                }
                initState = InitState.RESOLVABLE_MEMBER_CALL
            }
            initStates.add(
                checkAndBuildNodeContext(
                    cfgNode = node,
                    accessedMembers = accessedMembers,
                    accessedProperties = accessedProperties,
                    state = initState
                )
            )
        }

        override fun visitQualifiedAccessNode(node: QualifiedAccessNode) {
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
            if (node.fir.calleeReference.isMemberOfTheClass(classDeclaration.symbol.classId)) {
                // if it is a member and it is qualifiedAccess then it is property :)
                val member = node.fir.calleeReference.resolvedSymbolAsProperty!!
                accessedProperties.add(member)
            }
            initStates.add(
                checkAndBuildNodeContext(
                    cfgNode = node,
                    accessedMembers = accessedProperties,
                    accessedProperties = accessedProperties,
                    state = InitState.QUAILIFIED_ACCESS
                )
            )
        }

        override fun visitVariableAssignmentNode(node: VariableAssignmentNode) {
            if (node.fir.lValue.resolvedSymbolAsProperty?.callableId?.classId == classDeclaration.symbol.classId) {
                val accessedMembers = mutableListOf<FirCallableSymbol<*>>()
                val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
                val symbol = node.fir.lValue.resolvedSymbolAsProperty
                accessedMembers.add(symbol!!)
                accessedProperties.add(symbol)
                lastAssignmentContextNode =
                    checkAndBuildNodeContext(
                        cfgNode = node,
                        accessedMembers = accessedMembers,
                        accessedProperties = accessedProperties,
                        state = InitState.ASSINGMENT_OR_INITIALIZER
                    )
                isInRvalueOfAssignment = true
                lastAssignRValue = node.fir.rValue
                initStates.add(lastAssignmentContextNode!!)
            } else {
//                TODO:  properties assignment local vals?
                visitNode(node)
            }
        }

        override fun visitPropertyInitializerEnterNode(node: PropertyInitializerEnterNode) {
            if (node.fir.isClassPropertyWithInitializer) {
                isInPropertyInitializer = false
                val accessedProperties = listOf(node.fir.symbol)
                initStates.add(
                    checkAndBuildNodeContext(
                        cfgNode = node,
                        accessedMembers = accessedProperties,
                        accessedProperties = accessedProperties,
                        possibleAffectingNodes = lastPropertyInitializerAffectingNodes,
                        state = InitState.ASSINGMENT_OR_INITIALIZER
                    )
                )
                lastPropertyInitializerAffectingNodes = mutableListOf()

            } else {
                visitNode(node)
            }
        }

        override fun visitPropertyInitializerExitNode(node: PropertyInitializerExitNode) {
            if (node.fir.isClassPropertyWithInitializer) {
                isInPropertyInitializer = true
                val accessedProperties = listOf(node.fir.symbol)
                // TODO affected by itself also :)))
                lastPropertyInitializerContextNode = checkAndBuildNodeContext(
                    cfgNode = node,
                    accessedProperties = accessedProperties,
                    accessedMembers = accessedProperties,
                    state = InitState.ASSINGMENT_OR_INITIALIZER
                )
                initStates.add(lastPropertyInitializerContextNode!!)
            } else {
                visitNode(node)
            }
        }
//        TODO: lambda

        private fun checkAndBuildNodeContext(
            cfgNode: CFGNode<*>,
            accessedMembers: List<FirCallableSymbol<*>> = emptyList(),
            accessedProperties: List<FirVariableSymbol<*>> = emptyList(),
            initializedProperties: MutableList<FirVariableSymbol<*>> = mutableListOf(),
            possibleAffectedNodes: MutableList<CFGNode<*>> = mutableListOf(),
            possibleAffectingNodes: MutableList<CFGNode<*>> = mutableListOf(),
            state: InitState = InitState.NOT_AFFECTED
        ): InitializeContextNode {

            val context = InitializeContextNode(
                cfgNode,
                accessedMembers,
                accessedProperties,
                initializedProperties,
                possibleAffectedNodes,
                possibleAffectingNodes,
                state
            )
// TODO: if variable
            if (checkIfInRvalueOfAssignment(cfgNode))
                context.affectedNodes.add((lastAssignmentContextNode?.cfgNode)!!)

            if (checkIfInPropertyInitializer(cfgNode)) {
                context.affectedNodes.add((lastAssignmentContextNode?.cfgNode)!!)
            }

            return context
        }

        private fun checkIfInRvalueOfAssignment(node: CFGNode<*>): Boolean {
            if (isInRvalueOfAssignment) {
                lastAssignmentAffectingNodes.add(node)
                if (node.fir == lastAssignRValue) {
                    isInRvalueOfAssignment = false
                    lastAssignmentContextNode?.affectingNodes = lastAssignmentAffectingNodes
                    lastAssignmentAffectingNodes = mutableListOf()
                }
                return true
            }
            return false
        }

        private fun checkIfInPropertyInitializer(node: CFGNode<*>) =
            if (isInPropertyInitializer) {
                lastPropertyInitializerAffectingNodes.add(node)
                true
            } else false


    }
}
