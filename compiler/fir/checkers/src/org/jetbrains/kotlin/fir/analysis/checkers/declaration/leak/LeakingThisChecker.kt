/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.declarations.FirRegularClass

object LeakingThisChecker : FirDeclarationChecker<FirRegularClass>() {

    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {

        val maxResolvedCallLevel = 100
        val maxResolvedSuperTypesLevel = 4

        val classInitContext = if (declaration.isDerivedClass())
            DerivedClassInitContext(context.session, declaration)
        else BaseClassInitContext(declaration)

        val analyzer = InitContextAnalyzer(classInitContext, reporter, maxResolvedCallLevel, maxResolvedSuperTypesLevel)
        analyzer.analyze()
    }
}

