/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.modality

object LeakingThisChecker : FirDeclarationChecker<FirRegularClass>() {

    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        when (declaration.modality) {
            Modality.FINAL -> {
                if (!declaration.hasClassSomeParents())
                    runCheck(
                        collectDataForSimpleClassAnalysis(declaration),
                        reporter
                    )
            }
            else -> {

            }
        }
    }

    private fun collectDataForSimpleClassAnalysis(classDeclaration: FirRegularClass): BaseClassInitContext =
        BaseClassInitContext(
            classDeclaration
        )

    private fun runCheck(classInitContext: BaseClassInitContext, reporter: DiagnosticReporter) {
        val analyzer = InitContextAnalyzer(classInitContext, reporter, 100)
        analyzer.analyze()
    }
}

