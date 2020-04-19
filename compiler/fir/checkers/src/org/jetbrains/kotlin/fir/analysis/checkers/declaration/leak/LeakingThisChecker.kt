/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSourceElement
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.modality
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionCallNode
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol


object LeakingThisChecker : FirDeclarationChecker<FirRegularClass>() {

    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        val classDeclarationContext = when (declaration.modality) {
            Modality.FINAL -> {
                if (!declaration.hasClassSomeParents())
                    collectDataForSimpleClassAnalysis(
                        declaration
                    )
                else
                    TODO()
            }
            else -> TODO()
        }

        runCheck(
            classDeclarationContext,
            reporter
        )

    }

    private fun collectDataForSimpleClassAnalysis(classDeclaration: FirRegularClass): ClassDeclarationContext =
        ClassWithOutSuperDeclarationContext(
            classDeclaration
        )

    private fun runCheck(classDeclarationContext: ClassDeclarationContext, reporter: DiagnosticReporter) {
        if (classDeclarationContext.isClassNotRelevantForChecker)
            return

    }

    private fun checkConstructor(
        constructorDecl: FirConstructor,
        classDeclarationContext: ClassDeclarationContext,
        reporter: DiagnosticReporter
    ) {
        buildLatticeAndComputeFixedPoint(
            classDeclarationContext
        )
        reporter.report(constructorDecl.source)
    }

    private fun buildLatticeAndComputeFixedPoint(
        classDeclarationContext: ClassDeclarationContext,
    ) {
        if (!classDeclarationContext.isCfgAvailable)
            return

    }

    private fun DiagnosticReporter.report(source: FirSourceElement?) {
        source?.let { report(FirErrors.LEAKING_THIS_IN_CONSTRUCTOR.on(it, "Possible leaking this in constructor")) }
    }

    // always contains at least kotlin/Any as a parent
    private fun FirRegularClass.hasClassSomeParents() = this.superTypeRefs.size > 1
}

