/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSourceElement
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionCallNode
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol


object LeakingThisChecker : FirDeclarationChecker<FirRegularClass>() {

    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        val classDeclarationContext = when (declaration.modality) {
            Modality.FINAL -> {
                if (!declaration.hasClassSomeParents())
                    collectDataForSimpleClassAnalysis(declaration)
                else
                    TODO()
            }
            else -> TODO()
        }

        runCheck(classDeclarationContext, reporter)

    }

    private fun collectDataForSimpleClassAnalysis(classDeclaration: FirRegularClass): ClassDeclarationContext =
        ClassDeclarationContext(classDeclaration)

    private fun runCheck(classDeclarationContext: ClassDeclarationContext, reporter: DiagnosticReporter) {
        if (classDeclarationContext.isClassNotRelevantForChecker)
            return

        for (constructorDecl in classDeclarationContext.constructorsDeclList) {
            if (hasConstructorMemberCalls(constructorDecl))
                checkConstructor(constructorDecl, classDeclarationContext, reporter)
        }
    }

    private fun checkConstructor(
        constructorDecl: FirConstructor,
        classDeclarationContext: ClassDeclarationContext,
        reporter: DiagnosticReporter
    ) {
        buildLatticeAndComputeFixedPoint(constructorDecl, classDeclarationContext)
        reporter.report(constructorDecl.source)
    }

    private fun buildLatticeAndComputeFixedPoint(
        constructorDecl: FirConstructor,
        classDeclarationContext: ClassDeclarationContext,
    ) {
//        TODO("Not yet implemented")
    }

    private fun hasConstructorMemberCalls(
        constructorDecl: FirConstructor
    ): Boolean {
        if (constructorDecl.controlFlowGraphReference.controlFlowGraph == null) return false
        for (node in constructorDecl.controlFlowGraphReference.controlFlowGraph!!.nodes) {
            if (node is FunctionCallNode) {
                val classId =
                    ((node.fir.calleeReference as? FirResolvedNamedReference ?: continue)
                        .resolvedSymbol as? FirNamedFunctionSymbol ?: continue)
                        .callableId.classId
                // call member - fun in constructor
                if (classId != null && classId == constructorDecl.symbol.callableId.classId)
                    return true
            }
        }
        return false
    }

    private fun DiagnosticReporter.report(source: FirSourceElement?) {
        source?.let { report(FirErrors.LEAKING_THIS_IN_CONSTRUCTOR.on(it, "Possible leaking this in constructor")) }
    }

    // always contains at least kotlin/Any as a parent
    private fun FirRegularClass.hasClassSomeParents() = this.superTypeRefs.size > 1

    private class ClassDeclarationContext(val classDeclaration: FirRegularClass) {
        val constructorsDeclList = mutableListOf<FirConstructor>()
        val propsDeclList = mutableListOf<FirProperty>()
        val funDeclList = mutableListOf<FirSimpleFunction>()
        val isClassNotRelevantForChecker: Boolean
            get() = constructorsDeclList.isEmpty() || propsDeclList.isEmpty()

        init {
            for (declaration in classDeclaration.declarations) {
                when (declaration) {
                    is FirConstructor -> constructorsDeclList.add(declaration)
                    // add only not initialized props for analysis
                    is FirProperty -> if (declaration.isLeakingPossible) propsDeclList.add(declaration)
                    is FirSimpleFunction -> funDeclList.add(declaration)
                }
            }
        }

        private val FirProperty.isLeakingPossible: Boolean
            get() = initializer == null || isVar // TODO: add checking for nullability
    }
}

