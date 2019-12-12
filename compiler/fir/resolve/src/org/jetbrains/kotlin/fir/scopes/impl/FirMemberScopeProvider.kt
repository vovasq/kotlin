/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.scopes.impl

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.FirSessionComponent
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.resolve.memberScopeProvider
import org.jetbrains.kotlin.fir.scopes.FirScope
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class FirMemberScopeProvider : FirSessionComponent {

    private val declaredMemberCache = mutableMapOf<FirClass<*>, FirScope>()
    private val nestedClassifierCache = mutableMapOf<FirClass<*>, FirNestedClassifierScope>()
    private val selfImportingCache = mutableMapOf<FqName, FirSelfImportingScope>()

    fun declaredMemberScope(
        klass: FirClass<*>,
        useLazyNestedClassifierScope: Boolean,
        existingNames: List<Name>?
    ): FirScope {
        return declaredMemberCache.getOrPut(klass) {
            FirClassDeclaredMemberScope(klass, useLazyNestedClassifierScope, existingNames)
        }
    }

    fun nestedClassifierScope(klass: FirClass<*>): FirNestedClassifierScope {
        return nestedClassifierCache.getOrPut(klass) {
            FirNestedClassifierScope(klass)
        }
    }

    // TODO: it's better to cache this scope in ScopeSession
    fun selfImportingScope(fqName: FqName, session: FirSession): FirSelfImportingScope {
        return selfImportingCache.getOrPut(fqName) {
            FirSelfImportingScope(fqName, session)
        }
    }
}

fun declaredMemberScope(
    klass: FirClass<*>,
    useLazyNestedClassifierScope: Boolean = false,
    existingNames: List<Name>? = null
): FirScope {
    return klass
        .session
        .memberScopeProvider
        .declaredMemberScope(klass, useLazyNestedClassifierScope, existingNames)
}

fun nestedClassifierScope(klass: FirClass<*>): FirNestedClassifierScope {
    return klass
        .session
        .memberScopeProvider
        .nestedClassifierScope(klass)
}

fun nestedClassifierScope(classId: ClassId, session: FirSession, existingNames: List<Name>? = null): FirLazyNestedClassifierScope {
    return FirLazyNestedClassifierScope(classId, session, existingNames)
}

fun selfImportingScope(fqName: FqName, session: FirSession): FirSelfImportingScope {
    return session.memberScopeProvider.selfImportingScope(fqName, session)
}

