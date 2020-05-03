/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNode
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol

internal class InitializeContextNode(
    val cfgNode: CFGNode<*>,
    val accessedMembers: List<FirCallableSymbol<*>>,
    val accessedProperties: List<FirVariableSymbol<*>>,
    val initializedProperties: MutableList<FirVariableSymbol<*>>,
    var affectedNodes: MutableList<CFGNode<*>>, // TODO: change to InitStateNodeContext
    var affectingNodes: MutableList<CFGNode<*>>, // TODO: change to InitStateNodeContext
    var state: InitState
)

internal enum class InitState {
    ASSINGMENT_OR_INITIALIZER,
    QUAILIFIED_ACCESS,

    INIT_FAIL,
    INIT_OK,
    NOT_AFFECTED,
    RESOLVABLE_MEMBER_CALL,
    UNRESOLVABLE_FUN_CALL,
    UNRESOLVABLE,
    FULL_INIT_OK
}
