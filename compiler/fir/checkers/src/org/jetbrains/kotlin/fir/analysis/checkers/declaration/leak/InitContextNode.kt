/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNode
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol

class InitContextNode(
    val cfgNode: CFGNode<*>,
    val accessedMembers: List<FirCallableSymbol<*>>,
    val accessedProperties: List<FirVariableSymbol<*>>,
    val initCandidates: MutableList<FirVariableSymbol<*>>,
    var affectedNodes: MutableList<InitContextNode>, // nodes those depends on this node
    var affectingNodes: MutableList<InitContextNode>, // nodes those affect on this node
    var nodeType: ContextNodeType,
    var isInitialized: Boolean // if true => initCandidate contains initialized property
) {
    val initCandidate: FirVariableSymbol<*>
        get() = initCandidates[0]
    val firstAccessedProperty: FirVariableSymbol<*>
        get() = accessedProperties[0]

    fun confirmInitForCandidate() {
        isInitialized = true
    }
}

enum class ContextNodeType {
    ASSIGNMENT_OR_INITIALIZER,
    PROPERTY_QUALIFIED_ACCESS,
    PRIMARY_CONSTRUCTOR_PARAM_QUALIFIED_ACCESS,
    NOT_MEMBER_QUALIFIED_ACCESS,
    NOT_AFFECTED,
    RESOLVABLE_CALL, // top-level, local and members calls
    RESOLVABLE_THIS_RECEIVER_CALL,
    UNRESOLVABLE_FUN_CALL,
}
