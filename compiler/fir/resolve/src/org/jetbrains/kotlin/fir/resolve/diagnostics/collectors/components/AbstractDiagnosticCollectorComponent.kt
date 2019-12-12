/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.diagnostics.collectors.components

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.resolve.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.resolve.diagnostics.collectors.AbstractDiagnosticCollector
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

abstract class AbstractDiagnosticCollectorComponent(private val collector: AbstractDiagnosticCollector) : FirVisitorVoid() {
    override fun visitElement(element: FirElement) {}

    protected fun runCheck(block: (DiagnosticReporter) -> Unit) {
        collector.runCheck(block)
    }
}