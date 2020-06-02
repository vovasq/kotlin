/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.ConeLookupTagBasedType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.isAny
import org.jetbrains.kotlin.name.ClassId

// always contains at least kotlin/Any as a parent
internal fun FirRegularClass.isDerivedClass() = this.superTypeRefs.isNotEmpty() && !this.superTypeRefs[0].isAny


internal val FirReference.resolvedNamedReferenceSymbol: AbstractFirBasedSymbol<*>?
    get() = (this as? FirResolvedNamedReference)?.resolvedSymbol

internal val FirReference.resolvedSymbolAsProperty: FirPropertySymbol?
    get() = (this as? FirResolvedNamedReference)?.resolvedSymbol as? FirPropertySymbol

internal val FirReference.resolvedSymbolAsNamedFunction: FirNamedFunctionSymbol?
    get() = (this as? FirResolvedNamedReference)?.resolvedSymbol as? FirNamedFunctionSymbol

internal val FirReference.resolvedSymbolAsCallable: FirCallableSymbol<*>?
    get() = when {
        (resolvedSymbolAsNamedFunction != null) -> resolvedSymbolAsNamedFunction
        (resolvedSymbolAsProperty != null) -> resolvedSymbolAsProperty
        else -> null
    }

internal fun FirReference.isCallResolvable(expectedClassId: ClassId): Boolean {
    val resolvedSymbol = resolvedSymbolAsCallable ?: return false
    return resolvedSymbol.callableId.isLocal || resolvedSymbol.callableId.isTopLevel || expectedClassId == resolvedSymbol.callableId.classId
}

internal fun FirReference.isMemberOfTheClass(expectedClassId: ClassId): Boolean {
    val resolvedSymbol = resolvedSymbolAsCallable ?: return false
    val classId = resolvedSymbol.callableId.classId
    // call member - fun in constructor
    if (classId != null && classId == expectedClassId)
        return true
    return false
}


internal val FirProperty.isClassPropertyWithInitializer: Boolean
    get() = !this.isLocal && this.initializer != null


internal fun FirTypeRef.getFirSuperTypeRegularClass(session: FirSession): FirRegularClass? =
    (((this as? FirResolvedTypeRef)?.type as? ConeLookupTagBasedType)?.lookupTag?.toSymbol(session)?.fir as? FirRegularClass)

