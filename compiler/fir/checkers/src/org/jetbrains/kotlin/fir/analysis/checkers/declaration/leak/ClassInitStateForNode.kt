/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNode
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol

internal class ClassInitStateForNode(
    val cfgNode: CFGNode<*>,
    val accessedMembers: List<FirCallableSymbol<*>>,
    val accessedProperties: List<FirVariableSymbol<*>>,
    val initializedProperties: List<FirVariableSymbol<*>>,
    val state: InitState
)

internal enum class InitState {
    INIT_FAIL,
    INIT_OK,
    NOT_AFFECTED,
    POSSIBLE_WEAK,
    UNRESOLVABLE,
    FULL_INIT_OK
}
