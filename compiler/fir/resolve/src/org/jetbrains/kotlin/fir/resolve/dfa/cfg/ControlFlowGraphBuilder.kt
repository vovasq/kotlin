/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of graph source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.dfa.cfg

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.dfa.NodeStorage
import org.jetbrains.kotlin.fir.resolve.dfa.Stack
import org.jetbrains.kotlin.fir.resolve.dfa.stackOf
import org.jetbrains.kotlin.fir.resolve.transformers.resultType
import org.jetbrains.kotlin.fir.types.isNothing

open class ControlFlowGraphBuilder : ControlFlowGraphNodeBuilder() {
    private val graphs: Stack<ControlFlowGraph> = stackOf()
    override val graph: ControlFlowGraph get() = graphs.top()

    private val lexicalScopes: Stack<Stack<CFGNode<*>>> = stackOf()
    private val lastNodes: Stack<CFGNode<*>> get() = lexicalScopes.top()

    private val functionExitNodes: NodeStorage<FirFunction, FunctionExitNode> = NodeStorage()

    private val whenExitNodes: NodeStorage<FirWhenExpression, WhenExitNode> = NodeStorage()

    private val loopEnterNodes: NodeStorage<FirElement, CFGNode<FirElement>> = NodeStorage()
    private val loopExitNodes: NodeStorage<FirLoop, LoopExitNode> = NodeStorage()

    private val tryExitNodes: NodeStorage<FirTryExpression, TryExpressionExitNode> = NodeStorage()
    private val catchNodeStorages: Stack<NodeStorage<FirCatch, CatchClauseEnterNode>> = stackOf()
    private val catchNodeStorage: NodeStorage<FirCatch, CatchClauseEnterNode> get() = catchNodeStorages.top()

    private val binaryAndExitNodes: Stack<BinaryAndExitNode> = stackOf()
    private val binaryOrExitNodes: Stack<BinaryOrExitNode> = stackOf()

    override var levelCounter: Int = 0

    val lastNode: CFGNode<*> get() = lastNodes.top()

    // ----------------------------------- Callbacks -----------------------------------

    open fun passFlow(from: CFGNode<*>, to: CFGNode<*>) {}

    // ----------------------------------- Named function -----------------------------------

    fun enterNamedFunction(namedFunction: FirNamedFunction): FunctionEnterNode {
        graphs.push(ControlFlowGraph())
        lexicalScopes.push(stackOf())
        functionExitNodes.push(createFunctionExitNode(namedFunction))
        return createFunctionEnterNode(namedFunction).also { lastNodes.push(it) }.also { levelCounter++ }
    }

    fun exitNamedFunction(namedFunction: FirNamedFunction): ControlFlowGraph {
        levelCounter--
        val exitNode = functionExitNodes.pop()
        addEdge(lastNodes.pop(), exitNode)
        assert(exitNode.fir == namedFunction)
//        assert(levelCounter == 0)
        lexicalScopes.pop()
        return graphs.pop()
    }

    // ----------------------------------- Block -----------------------------------

    fun enterBlock(block: FirBlock): BlockEnterNode {
        return createBlockEnterNode(block).also { addNewSimpleNode(it) }.also { levelCounter++ }
    }

    fun exitBlock(block: FirBlock): BlockExitNode {
        levelCounter--
        return createBlockExitNode(block).also { addNewSimpleNode(it) }
    }

    // ----------------------------------- Type operator call -----------------------------------

    fun exitTypeOperatorCall(typeOperatorCall: FirTypeOperatorCall): TypeOperatorCallNode {
        return createTypeOperatorCallNode(typeOperatorCall).also { addNewSimpleNode(it) }
    }

    // ----------------------------------- Jump -----------------------------------

    fun exitJump(jump: FirJump<*>): JumpNode {
        val node = createJumpNode(jump)
        val nextNode = when (jump) {
            is FirReturnExpression -> functionExitNodes[jump.target.labeledElement]
            is FirContinueExpression -> loopEnterNodes[jump.target.labeledElement]
            is FirBreakExpression -> loopExitNodes[jump.target.labeledElement]
            else -> throw IllegalArgumentException("Unknown jump type: ${jump.render()}")
        }
        addNodeWithJump(node, nextNode)
        return node
    }

    // ----------------------------------- When -----------------------------------

    fun enterWhenExpression(whenExpression: FirWhenExpression): WhenEnterNode {
        val node = createWhenEnterNode(whenExpression)
        addNewSimpleNode(node)
        whenExitNodes.push(createWhenExitNode(whenExpression))
        levelCounter++
        return node
    }

    fun enterWhenBranchCondition(whenBranch: FirWhenBranch): WhenBranchConditionEnterNode {
        return createWhenBranchConditionEnterNode(whenBranch).also { addNewSimpleNode(it) }.also { levelCounter++ }
    }

    fun exitWhenBranchCondition(whenBranch: FirWhenBranch): WhenBranchConditionExitNode {
        levelCounter--
        return createWhenBranchConditionExitNode(whenBranch).also {
            addNewSimpleNode(it)
            // put exit branch condition node twice so we can refer it after exit from when expression
            lastNodes.push(it)
        }.also { levelCounter++ }
    }

    fun exitWhenBranchResult(whenBranch: FirWhenBranch): WhenBranchResultExitNode {
        levelCounter--
        val node = createWhenBranchResultExitNode(whenBranch)
        addEdge(lastNodes.pop(), node)
        val whenExitNode = whenExitNodes.top()
        addEdge(node, whenExitNode, propagateDeadness = false)
        return node
    }

    fun exitWhenExpression(whenExpression: FirWhenExpression): WhenExitNode {
        levelCounter--
        // exit from last condition node still on stack
        // we should remove it
        require(lastNodes.pop() is WhenBranchConditionExitNode)
        val whenExitNode = whenExitNodes.pop()
        lastNodes.push(whenExitNode)
        return whenExitNode
    }

    // ----------------------------------- While Loop -----------------------------------

    fun enterWhileLoop(loop: FirLoop): LoopConditionEnterNode {
        addNewSimpleNode(createLoopEnterNode(loop))
        loopExitNodes.push(createLoopExitNode(loop))
        levelCounter++
        val node = createLoopConditionEnterNode(loop)
        levelCounter++
        addNewSimpleNode(node)
        // put conditional node twice so we can refer it after exit from loop block
        lastNodes.push(node)
        loopEnterNodes.push(node)
        return node
    }

    fun exitWhileLoopCondition(loop: FirLoop): LoopConditionExitNode {
        levelCounter--
        val conditionExitNode = createLoopConditionExitNode(loop)
        addNewSimpleNode(conditionExitNode)
        // TODO: here we can check that condition is always true
        addEdge(conditionExitNode, loopExitNodes.top())
        addNewSimpleNode(createLoopBlockEnterNode(loop))
        levelCounter++
        return conditionExitNode
    }

    fun exitWhileLoop(loop: FirLoop): CFGNode<*> {
        loopEnterNodes.pop()
        levelCounter--
        val loopBlockExitNode = createLoopBlockExitNode(loop)
        addEdge(lastNodes.pop(), loopBlockExitNode)
        val conditionEnterNode = lastNodes.pop()
        require(conditionEnterNode is LoopConditionEnterNode)
        addEdge(loopBlockExitNode, conditionEnterNode, propagateDeadness = false)
        lastNodes.push(loopExitNodes.pop())
        levelCounter--
        return conditionEnterNode
    }

    // ----------------------------------- Do while Loop -----------------------------------

    fun enterDoWhileLoop(loop: FirLoop) {
        addNewSimpleNode(createLoopEnterNode(loop))
        loopExitNodes.push(createLoopExitNode(loop))
        levelCounter++
        val blockEnterNode = createLoopBlockEnterNode(loop)
        addNewSimpleNode(blockEnterNode)
        // put block enter node twice so we can refer it after exit from loop condition
        lastNodes.push(blockEnterNode)
        loopEnterNodes.push(blockEnterNode)
        levelCounter++
    }

    fun enterDoWhileLoopCondition(loop: FirLoop) {
        levelCounter--
        addNewSimpleNode(createLoopBlockExitNode(loop))
        addNewSimpleNode(createLoopConditionEnterNode(loop))
        levelCounter++
    }

    fun exitDoWhileLoop(loop: FirLoop) {
        loopEnterNodes.pop()
        levelCounter--
        val conditionExitNode = createLoopConditionExitNode(loop)
        // TODO: here we can check that condition is always false
        addEdge(lastNodes.pop(), conditionExitNode)
        val blockEnterNode = lastNodes.pop()
        require(blockEnterNode is LoopBlockEnterNode)
        addEdge(conditionExitNode, blockEnterNode, propagateDeadness = false)
        val loopExit = loopExitNodes.pop()
        addEdge(conditionExitNode, loopExit)
        lastNodes.push(loopExit)
        levelCounter--
    }

    // ----------------------------------- Boolean operators -----------------------------------

    fun enterBinaryAnd(binaryLogicExpression: FirBinaryLogicExpression): BinaryAndEnterNode {
        assert(binaryLogicExpression.kind == FirBinaryLogicExpression.OperationKind.AND)
        binaryAndExitNodes.push(createBinaryAndExitNode(binaryLogicExpression))
        return createBinaryAndEnterNode(binaryLogicExpression).also { addNewSimpleNode(it) }.also { levelCounter++ }
    }

    fun exitLeftBinaryAndArgument(binaryLogicExpression: FirBinaryLogicExpression) {
        assert(binaryLogicExpression.kind == FirBinaryLogicExpression.OperationKind.AND)
        addEdge(lastNode, binaryAndExitNodes.top())
    }

    fun exitBinaryAnd(binaryLogicExpression: FirBinaryLogicExpression): BinaryAndExitNode {
        levelCounter--
        assert(binaryLogicExpression.kind == FirBinaryLogicExpression.OperationKind.AND)
        return binaryAndExitNodes.pop().also { addNewSimpleNode(it) }
    }

    fun enterBinaryOr(binaryLogicExpression: FirBinaryLogicExpression): BinaryOrEnterNode {
        assert(binaryLogicExpression.kind == FirBinaryLogicExpression.OperationKind.OR)
        binaryOrExitNodes.push(createBinaryOrExitNode(binaryLogicExpression))
        return createBinaryOrEnterNode(binaryLogicExpression).also {
            addNewSimpleNode(it)
            // put or enter node twice so we can refer it after exit from left argument
            lastNodes.push(it)
        }.also { levelCounter++ }
    }

    fun exitLeftBinaryOrArgument(binaryLogicExpression: FirBinaryLogicExpression) {
        levelCounter--
        assert(binaryLogicExpression.kind == FirBinaryLogicExpression.OperationKind.OR)
        addEdge(lastNodes.pop(), binaryOrExitNodes.top())
    }

    fun exitBinaryOr(binaryLogicExpression: FirBinaryLogicExpression): BinaryOrExitNode {
        assert(binaryLogicExpression.kind == FirBinaryLogicExpression.OperationKind.OR)
        return binaryOrExitNodes.pop().also { addNewSimpleNode(it) }
    }

    // ----------------------------------- Try-catch-finally -----------------------------------

    fun enterTryExpression(tryExpression: FirTryExpression): TryMainBlockEnterNode {
        catchNodeStorages.push(NodeStorage())
        addNewSimpleNode(createTryExpressionEnterNode(tryExpression))
        tryExitNodes.push(createTryExpressionExitNode(tryExpression))
        levelCounter++
        val tryNode = createTryMainBlockEnterNode(tryExpression)
        addNewSimpleNode(tryNode)
        addEdge(tryNode, functionExitNodes.top())

        for (catch in tryExpression.catches) {
            val catchNode = createCatchClauseEnterNode(catch)
            catchNodeStorage.push(catchNode)
            addEdge(tryNode, catchNode)
            addEdge(catchNode, functionExitNodes.top())
        }
        levelCounter++
        // TODO: add finally
        return tryNode
    }

    fun exitTryMainBlock(tryExpression: FirTryExpression): TryMainBlockExitNode {
        levelCounter--
        val node = createTryMainBlockExitNode(tryExpression)
        addEdge(lastNodes.pop(), node)
        addEdge(node, tryExitNodes.top())
        return node
    }

    fun enterCatchClause(catch: FirCatch): CatchClauseEnterNode {
        return catchNodeStorage[catch].also { lastNodes.push(it) }.also { levelCounter++ }
    }

    fun exitCatchClause(catch: FirCatch): CatchClauseExitNode {
        levelCounter--
        return createCatchClauseExitNode(catch).also {
            addEdge(lastNodes.pop(), it)
            addEdge(it, tryExitNodes.top(), propagateDeadness = false)
        }
    }

    fun enterFinallyBlock(tryExpression: FirTryExpression) { /*TODO*/ }

    fun exitFinallyBlock(tryExpression: FirTryExpression) { /*TODO*/ }

    fun exitTryExpression(tryExpression: FirTryExpression): TryExpressionExitNode {
        levelCounter--
        catchNodeStorages.pop()
        val node = tryExitNodes.pop()
        node.markAsDeadIfNecessary()
        lastNodes.push(node)
        return node
    }

    // ----------------------------------- Resolvable call -----------------------------------

    fun exitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression): QualifiedAccessNode {
        val returnsNothing = qualifiedAccessExpression.resultType.isNothing
        val node = createQualifiedAccessNode(qualifiedAccessExpression, returnsNothing)
        if (returnsNothing) {
            addNodeThatReturnsNothing(node)
        } else {
            addNewSimpleNode(node)
        }
        return node
    }

    fun exitFunctionCall(functionCall: FirFunctionCall): FunctionCallNode {
        val returnsNothing = functionCall.resultType.isNothing
        val node = createFunctionCallNode(functionCall, returnsNothing)
        if (returnsNothing) {
            addNodeThatReturnsNothing(node)
        } else {
            addNewSimpleNode(node)
        }
        return node
    }

    fun exitConstExpresion(constExpression: FirConstExpression<*>): ConstExpressionNode {
        return createConstExpressionNode(constExpression).also { addNewSimpleNode(it) }
    }

    fun exitVariableDeclaration(variable: FirVariable<*>): VariableDeclarationNode {
        return createVariableDeclarationNode(variable).also { addNewSimpleNode(it) }
    }

    fun exitVariableAssignment(assignment: FirVariableAssignment): VariableAssignmentNode {
        return createVariableAssignmentNode(assignment).also { addNewSimpleNode(it) }
    }

    fun exitThrowExceptionNode(throwExpression: FirThrowExpression): ThrowExceptionNode {
        return createThrowExceptionNode(throwExpression).also { addNodeThatReturnsNothing(it) }
    }

    // -------------------------------------------------------------------------------------------------------------------------

    private fun CFGNode<*>.markAsDeadIfNecessary() {
        isDead = previousNodes.all { it.isDead }
    }

    private fun addNodeThatReturnsNothing(node: CFGNode<*>) {
        addNodeWithJump(node, functionExitNodes.top())
    }

    private fun addNodeWithJump(node: CFGNode<*>, targetNode: CFGNode<*>) {
        addEdge(lastNodes.pop(), node)
        addEdge(node, targetNode)
        val stub = createStubNode()
        addEdge(node, stub)
        lastNodes.push(stub)
    }

    private fun addNewSimpleNode(newNode: CFGNode<*>): CFGNode<*> {
        val oldNode = lastNodes.pop()
        addEdge(oldNode, newNode)
        lastNodes.push(newNode)
        return oldNode
    }

    private fun addEdge(from: CFGNode<*>, to: CFGNode<*>, shouldPassFlow: Boolean = true, propagateDeadness: Boolean = true) {
        if (propagateDeadness && from.isDead) {
            to.isDead = true
        }
        from.followingNodes += to
        to.previousNodes += from
        if (shouldPassFlow) {
            passFlow(from, to)
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------


}