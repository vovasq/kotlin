/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.analysis.cfa.traverseForwardWithoutLoops
import org.jetbrains.kotlin.fir.declarations.FirAnonymousInitializer
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.*
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.name.ClassId
import java.util.*

internal class BaseClassInitContext(private val classDeclaration: FirRegularClass) {
    val classId: ClassId
        get() = classDeclaration.symbol.classId

    val classInitContextNodesMap = mutableMapOf<CFGNode<*>, InitContextNode>()

    val isCfgAvailable: Boolean
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph != null

    val classCfg: ControlFlowGraph
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph!!

    private val propsDeclList = mutableListOf<FirProperty>()
    private val anonymousInitializer = mutableListOf<FirAnonymousInitializer>()

//    private val highOrderFunctions


    init {
        if (isCfgAvailable)
            for (subGraph in classCfg.subGraphs) {
                when (val declaration = subGraph.declaration) {
                    is FirAnonymousInitializer -> anonymousInitializer.add(declaration)
                    is FirProperty -> propsDeclList.add(declaration)
                }
            }
        val visitor = ForwardCfgVisitor(classDeclaration.symbol.classId)
        classCfg.traverseForwardWithoutLoops(visitor)
        classInitContextNodesMap.putAll(visitor.initContextNodesMap)
    }
}

internal class ForwardCfgVisitor(
    private val classId: ClassId,
) : ControlFlowGraphVisitorVoid() {

    val initContextNodesMap = mutableMapOf<CFGNode<*>, InitContextNode>()

    private var currentAffectingNodes = Stack<InitContextNode>()

    private var isInPropertyInitializer = false
    private var lastPropertyInitializerAffectingNodes = mutableListOf<InitContextNode>()
    private var lastPropertyInitializerContextNode: InitContextNode? = null

    // for safeCall detection
    private var lastQualifiedAccessContextNode: InitContextNode? = null

    override fun visitNode(node: CFGNode<*>) {
        val context = checkAndBuildNodeContext(cfgNode = node)
        initContextNodesMap[node] = context
    }

    override fun visitFunctionCallNode(node: FunctionCallNode) {
        val accessedMembers = mutableListOf<FirCallableSymbol<*>>()
        val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
        var nodeType = ContextNodeType.UNRESOLVABLE_FUN_CALL
        if (node.fir.calleeReference.isMemberOfTheClass(classId)) {
            accessedMembers.add(node.fir.calleeReference.resolvedSymbolAsNamedFunction!!)
            for (argument in node.fir.argumentList.arguments) {
                if ((argument is FirQualifiedAccessExpression)
                    && argument.calleeReference.isMemberOfTheClass(classId)
                ) {
//                    TODO: clean up with casting
                    accessedMembers.add(argument.calleeReference.resolvedSymbolAsCallable!!)
                    if (argument.calleeReference.resolvedSymbolAsProperty != null)
                        accessedProperties.add(argument.calleeReference.resolvedSymbolAsProperty!!)
                }
            }
            nodeType = ContextNodeType.RESOLVABLE_MEMBER_CALL
        }
        val context = checkAndBuildNodeContext(
            cfgNode = node,
            accessedMembers = accessedMembers,
            accessedProperties = accessedProperties,
            nodeType = nodeType
        )

        initContextNodesMap[node] = context
    }

    override fun visitQualifiedAccessNode(node: QualifiedAccessNode) {
        val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
        var nodeType = ContextNodeType.NOT_MEMBER_QUALIFIED_ACCESS
        if (node.fir.calleeReference.isMemberOfTheClass(classId)) {
            // if it is a member and it is qualifiedAccess then it is property :)
            val member = node.fir.calleeReference.resolvedSymbolAsProperty!!
            nodeType = ContextNodeType.PROPERTY_QUALIFIED_ACCESS
            accessedProperties.add(member)
            lastQualifiedAccessContextNode = checkAndBuildNodeContext(
                cfgNode = node,
                accessedMembers = accessedProperties,
                accessedProperties = accessedProperties,
                nodeType = nodeType
            )
            initContextNodesMap[node] = lastQualifiedAccessContextNode!!
        } else {
            val context = checkAndBuildNodeContext(
                cfgNode = node,
                accessedMembers = accessedProperties,
                accessedProperties = accessedProperties,
                nodeType = nodeType
            )
            initContextNodesMap[node] = context
        }
    }

    override fun visitVariableAssignmentNode(node: VariableAssignmentNode) {
        if (node.fir.lValue.resolvedSymbolAsProperty?.callableId?.classId == classId) {
            val symbol = node.fir.lValue.resolvedSymbolAsProperty
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
            accessedProperties.add(symbol!!)
            val assignNodeContext = checkAndBuildNodeContext(
                cfgNode = node,
                accessedMembers = accessedProperties,
                accessedProperties = accessedProperties,
                initCandidates = accessedProperties,
                nodeType = ContextNodeType.ASSIGNMENT_OR_INITIALIZER
            )
            updateAffectedNodesAfterAssignmentNode(assignNodeContext)
            initContextNodesMap[node] = assignNodeContext
        } else {
            visitNode(node)
        }
    }

    override fun visitPropertyInitializerEnterNode(node: PropertyInitializerEnterNode) {
        if (node.fir.isClassPropertyWithInitializer) {
            isInPropertyInitializer = true
            currentAffectingNodes = Stack()
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>(node.fir.symbol)

            lastPropertyInitializerContextNode = checkAndBuildNodeContext(
                cfgNode = node,
                accessedMembers = accessedProperties,
                accessedProperties = accessedProperties,
                initCandidates = accessedProperties,
                nodeType = ContextNodeType.ASSIGNMENT_OR_INITIALIZER
            )
            initContextNodesMap[node] = lastPropertyInitializerContextNode!!
            lastPropertyInitializerAffectingNodes = mutableListOf()
        } else {
            visitNode(node)
        }
    }

    override fun visitPropertyInitializerExitNode(node: PropertyInitializerExitNode) {
        if (node.fir.isClassPropertyWithInitializer && isInPropertyInitializer) {
            isInPropertyInitializer = false
            val accessedProperties = mutableListOf<FirVariableSymbol<*>>(node.fir.symbol)
            // TODO affected by itself also :)))
            val context = checkAndBuildNodeContext(
                cfgNode = node,
                accessedMembers = accessedProperties,
                accessedProperties = accessedProperties,
                initCandidates = accessedProperties,
                affectingNodes = currentAffectingNodes,
                nodeType = ContextNodeType.ASSIGNMENT_OR_INITIALIZER
            )

            initContextNodesMap[node] = context
        } else {
            isInPropertyInitializer = false
            visitNode(node)
        }

    }
//        TODO: lambda

    override fun visitEnterSafeCallNode(node: EnterSafeCallNode) {
        if (lastQualifiedAccessContextNode == currentAffectingNodes.peek()) {
            lastQualifiedAccessContextNode?.nodeType = ContextNodeType.PROPERTY_SAFE_QUALIFIED_ACCESS
        }
        visitNode(node)
    }

    private fun checkAndBuildNodeContext(
        cfgNode: CFGNode<*>,
        accessedMembers: List<FirCallableSymbol<*>> = emptyList(),
        accessedProperties: List<FirVariableSymbol<*>> = emptyList(),
        initCandidates: MutableList<FirVariableSymbol<*>> = mutableListOf(),
        affectedNodes: MutableList<InitContextNode> = mutableListOf(),
        affectingNodes: MutableList<InitContextNode> = mutableListOf(),
        nodeType: ContextNodeType = ContextNodeType.NOT_AFFECTED,
        isInitialized: Boolean = false
    ): InitContextNode {

        val context = InitContextNode(
            cfgNode,
            accessedMembers,
            accessedProperties,
            initCandidates,
            affectedNodes,
            affectingNodes,
            nodeType,
            isInitialized
        )

        currentAffectingNodes.push(context)

        if (checkIfInPropertyInitializer(context)) {
            context.affectedNodes.add(lastPropertyInitializerContextNode!!)
        }

        return context
    }

    private fun updateAffectedNodesAfterAssignmentNode(assignNodeContext: InitContextNode) {
        val rValue = (assignNodeContext.cfgNode as VariableAssignmentNode).fir.rValue
        currentAffectingNodes.pop() // remove assignNodeContext itself
        var node = currentAffectingNodes.pop()
        while (node.cfgNode.fir != rValue) {
            node.affectedNodes.add(assignNodeContext)
            assignNodeContext.affectingNodes.add(node)
            node = currentAffectingNodes.pop()
        }
        currentAffectingNodes = Stack()
    }

    private fun checkIfInPropertyInitializer(context: InitContextNode) =
        if (context.cfgNode !is PropertyInitializerEnterNode && isInPropertyInitializer) {
            lastPropertyInitializerAffectingNodes.add(context)
            true
        } else false
}
