/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.declarations.FirAnonymousInitializer
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ControlFlowGraph
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph

internal abstract class ClassDeclarationContext {
    abstract val classDeclaration: FirRegularClass
    abstract val propsDeclList: List<FirProperty>
    abstract val anonymousInitializer: List<FirAnonymousInitializer>
    abstract val isClassNotRelevantForChecker: Boolean
    abstract val classInitStates: List<ClassInitStateForNode>

    val isCfgAvailable: Boolean
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph != null

    val classCfg: ControlFlowGraph
        get() = classDeclaration.controlFlowGraphReference.controlFlowGraph!!

}