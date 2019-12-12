/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.scopes.impl

import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.NAME_FOR_BACKING_FIELD
import org.jetbrains.kotlin.fir.scopes.FirScope
import org.jetbrains.kotlin.fir.scopes.ProcessorAction
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.Name

class FirLocalScope : FirScope() {

    val properties = mutableMapOf<Name, FirVariableSymbol<*>>()
    val functions = mutableMapOf<Name, FirFunctionSymbol<*>>()
    val classes = mutableMapOf<Name, FirRegularClassSymbol>()

    fun storeDeclaration(declaration: FirNamedDeclaration) {
        when (declaration) {
            is FirVariable<*> -> properties[declaration.name] = declaration.symbol
            is FirSimpleFunction -> functions[declaration.name] = declaration.symbol as FirNamedFunctionSymbol
            is FirRegularClass -> classes[declaration.name] = declaration.symbol
        }
    }

    fun storeBackingField(property: FirProperty) {
        properties[NAME_FOR_BACKING_FIELD] = property.backingFieldSymbol
    }

    override fun processFunctionsByName(name: Name, processor: (FirFunctionSymbol<*>) -> ProcessorAction): ProcessorAction {
        val function = functions[name]
        if (function != null) {
            return processor(function)
        }
        return ProcessorAction.NONE
    }

    override fun processPropertiesByName(name: Name, processor: (FirCallableSymbol<*>) -> ProcessorAction): ProcessorAction {
        val property = properties[name]
        if (property != null) {
            return processor(property)
        }
        return ProcessorAction.NONE
    }

    override fun processClassifiersByName(name: Name, processor: (FirClassifierSymbol<*>) -> ProcessorAction): ProcessorAction {
        val klass = classes[name]
        if (klass != null) {
            return processor(klass)
        }
        return ProcessorAction.NONE
    }
}