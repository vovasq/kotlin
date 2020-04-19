/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.ClassId


internal val FirReference.resolvedSymbolAsProperty: FirPropertySymbol?
    get() = (this as? FirResolvedNamedReference)?.resolvedSymbol as? FirPropertySymbol

internal val FirReference.resolvedSymbolAsNamedFunction: FirNamedFunctionSymbol?
    get() = (this as? FirResolvedNamedReference)?.resolvedSymbol as? FirNamedFunctionSymbol

internal fun FirReference.isCalleeReferenceMemberOfTheClass(expectedClassId: ClassId): Boolean {
    val resolvedSymbol = when {
        (resolvedSymbolAsNamedFunction != null) -> resolvedSymbolAsNamedFunction
        (resolvedSymbolAsProperty != null) -> resolvedSymbolAsProperty
        else -> return false
    }
    val classId = resolvedSymbol?.callableId?.classId
    // call member - fun in constructor
    if (classId != null && classId == expectedClassId)
        return true
    return false
}