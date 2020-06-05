/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.name.Name


@OptIn(ExperimentalStdlibApi::class)
internal class DerivedClassInitContext(
    classDeclaration: FirRegularClass
) : ClassInitContext(classDeclaration) {

    val overrideFunctions = mutableMapOf<Name, FirSimpleFunction>()
    val superTypesInitContexts = ArrayDeque<ClassInitContext>()

    init {
        if (isCfgAvailable) {
            superTypesInitContexts.addLast(this)
            // collect all open functions
            for (declaration in classDeclaration.declarations) {
                if (declaration is FirSimpleFunction && declaration.status.isOverride) overrideFunctions[declaration.name] = declaration
            }
        }
    }
}
