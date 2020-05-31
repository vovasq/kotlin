/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirThisReceiverExpression
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.*
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.name.ClassId
import java.util.*

internal abstract class ClassInitContext {

    abstract val classDeclaration: FirRegularClass

    abstract val classAnonymousFunctions: MutableMap<FunctionEnterNode, MutableMap<CFGNode<*>, InitContextNode>>
    abstract val primaryConstructorParams: MutableList<FirVariableSymbol<*>>
    abstract val classInitContextNodesMap: MutableMap<CFGNode<*>, InitContextNode>

    val classId: ClassId
        get() = classDeclaration.symbol.classId

    val isDerivedClassInitContext: Boolean
        get() = classDeclaration.isDerivedClass()

    val isCfgAvailable: Boolean
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph != null

    val classCfg: ControlFlowGraph
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph!!

}

internal class ForwardCfgVisitor(
    private val classId: ClassId,
    private val classAnonymousFunctions: MutableMap<FunctionEnterNode, MutableMap<CFGNode<*>, InitContextNode>>,
    private val primaryConstructorParams: MutableList<FirVariableSymbol<*>> = mutableListOf()
) : ControlFlowGraphVisitorVoid() {

    val initContextNodesMap = mutableMapOf<CFGNode<*>, InitContextNode>()

    private var currentAffectingNodes = Stack<InitContextNode>()

    private var isInPropertyInitializer = false
    private var lastPropertyInitializerContextNode: InitContextNode? = null

    // @key is node which will affected by anonymous function,
    // @value is enter node to anonymous function, by it  we can find all affecting nodes in classAnonymousFunctions
    private val delayedNodes = mutableMapOf<CFGNode<*>, CFGNode<*>>()

    // for safeCall detection
    private var lastQualifiedAccessContextNode: InitContextNode? = null

    private val isLastNodeThisReceiver: Boolean
        get() = if (currentAffectingNodes.isEmpty()) false
        else currentAffectingNodes.peek().nodeType == ContextNodeType.RESOLVABLE_THIS_RECEIVER_CALL

    override fun visitNode(node: CFGNode<*>) {
        val context = checkAndBuildNodeContext(cfgNode = node)
        initContextNodesMap[node] = context
    }

    override fun visitFunctionCallNode(node: FunctionCallNode) {
        val accessedMembers = mutableListOf<FirCallableSymbol<*>>()
        val accessedProperties = mutableListOf<FirVariableSymbol<*>>()
        var nodeType = ContextNodeType.UNRESOLVABLE_FUN_CALL
        if (node.fir.calleeReference.isCallResolvable(classId)) {
            accessedMembers.add(node.fir.calleeReference.resolvedSymbolAsNamedFunction!!)
            nodeType = ContextNodeType.RESOLVABLE_CALL
        }
        for (argument in node.fir.argumentList.arguments)
            when {
                argument is FirThisReceiverExpression -> {
                    nodeType = ContextNodeType.RESOLVABLE_THIS_RECEIVER_CALL
                }
                argument is FirQualifiedAccessExpression && argument.calleeReference.isMemberOfTheClass(classId) -> {
                    accessedMembers.add(argument.calleeReference.resolvedSymbolAsCallable!!)
                    accessedProperties.add(argument.calleeReference.resolvedSymbolAsProperty ?: continue)
                }
                else -> continue
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
        when {
            node.fir.calleeReference.isMemberOfTheClass(classId) -> {
                if (isLastNodeThisReceiver) {// case: "this.property"
                    currentAffectingNodes.peek().nodeType = ContextNodeType.NOT_AFFECTED
                }
                // if it is a member and it is qualifiedAccess then it is property :)
                val member = node.fir.calleeReference.resolvedSymbolAsProperty!!
                accessedProperties.add(member)
                lastQualifiedAccessContextNode = checkAndBuildNodeContext(
                    cfgNode = node,
                    accessedMembers = accessedProperties,
                    accessedProperties = accessedProperties,
                    nodeType = ContextNodeType.PROPERTY_QUALIFIED_ACCESS
                )
                initContextNodesMap[node] = lastQualifiedAccessContextNode!!
            }
            else -> {
                var nodeType = ContextNodeType.NOT_MEMBER_QUALIFIED_ACCESS
                if (node.fir.calleeReference.resolvedNamedReferenceSymbol in primaryConstructorParams) {
                    nodeType = ContextNodeType.PRIMARY_CONSTRUCTOR_PARAM_QUALIFIED_ACCESS
                }
                val context = checkAndBuildNodeContext(
                    cfgNode = node,
                    accessedMembers = accessedProperties,
                    accessedProperties = accessedProperties,
                    nodeType = nodeType
                )
                initContextNodesMap[node] = context
            }
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
                nodeType = ContextNodeType.NOT_AFFECTED
            )
            initContextNodesMap[node] = lastPropertyInitializerContextNode!!
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
                affectingNodes = currentAffectingNodes.clone() as MutableList<InitContextNode>,
                nodeType = ContextNodeType.ASSIGNMENT_OR_INITIALIZER
            )
            initContextNodesMap[node] = context
        } else {
            isInPropertyInitializer = false
            visitNode(node)
        }

    }

    override fun visitEnterSafeCallNode(node: EnterSafeCallNode) {
        if (lastQualifiedAccessContextNode == currentAffectingNodes.peek()) {
            lastQualifiedAccessContextNode?.nodeType = ContextNodeType.PROPERTY_SAFE_QUALIFIED_ACCESS
        }
        visitNode(node)
    }

    override fun visitFunctionEnterNode(node: FunctionEnterNode) {
        if (node.fir is FirAnonymousFunction && node.previousNodes.any { it is PropertyInitializerEnterNode }) {
            currentAffectingNodes = Stack()
        }
        super.visitFunctionEnterNode(node)
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

        if (context.cfgNode !is PropertyInitializerEnterNode && isInPropertyInitializer) {
            context.affectedNodes.add(lastPropertyInitializerContextNode!!)
        }

        checkForAnonymousAffects(context)

        return context
    }

    private fun checkForAnonymousAffects(context: InitContextNode) {
        val anonymousFunctionEnter =
            context.cfgNode.followingNodes.firstOrNull { it is FunctionEnterNode && it.fir is FirAnonymousFunction }
        if (anonymousFunctionEnter != null) {
            val affectedNode = context.cfgNode.followingNodes.firstOrNull {
                it is VariableAssignmentNode || it is VariableDeclarationNode || it is PropertyInitializerEnterNode
            }
            if (affectedNode != null) delayedNodes[affectedNode] = anonymousFunctionEnter
        }
        if (context.cfgNode in delayedNodes.keys) {
            context.affectingNodes.addAll(classAnonymousFunctions[delayedNodes[context.cfgNode]]?.values ?: return)
        }
    }

    private fun updateAffectedNodesAfterAssignmentNode(assignNodeContext: InitContextNode) {
        val rValue = (assignNodeContext.cfgNode as VariableAssignmentNode).fir.rValue
        currentAffectingNodes.pop() // remove assignNodeContext itself
        if (currentAffectingNodes.isNotEmpty()) {
            var node = currentAffectingNodes.pop()
            while (node.cfgNode.fir != rValue) {
                node.affectedNodes.add(assignNodeContext)
                assignNodeContext.affectingNodes.add(node)
                node = currentAffectingNodes.pop()
            }
        }
        currentAffectingNodes = Stack()
    }

}
