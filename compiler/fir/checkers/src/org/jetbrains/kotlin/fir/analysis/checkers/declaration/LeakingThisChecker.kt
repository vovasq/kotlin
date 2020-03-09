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
import org.jetbrains.kotlin.fir.expressions.FirThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ControlFlowGraph
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionCallNode
import org.jetbrains.kotlin.fir.resolve.dfa.controlFlowGraph


object LeakingThisChecker : FirDeclarationChecker<FirRegularClass>() {
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        for (memberDeclaration in declaration.declarations) {
            when (memberDeclaration) {
                is FirConstructor -> {
                    if (memberDeclaration.controlFlowGraphReference.controlFlowGraph != null)
                        analyzeAndReport(
                            memberDeclaration.controlFlowGraphReference.controlFlowGraph!!,
                            reporter
                        )
                }
                is FirAnonymousInitializer -> {
//                    FIXME seems to be FirAnonymousInitializer should also have controlFlowGraphReference
//                    this case is pretty the same as FirConstructor
                }
                is FirCallableMemberDeclaration<*> -> {
                    if (declaration.status.modality == Modality.OPEN && memberDeclaration.status.isOverride) {
                        reporter.report(memberDeclaration.source)
                    }
                }
                is FirProperty -> {

                }

            }
        }
    }

    private fun DiagnosticReporter.report(source: FirSourceElement?) {
        source?.let { report(FirErrors.LEAKING_THIS_IN_CONSTRUCTOR.on(it, "Possible leaking this in constructor")) }
    }

    private fun analyzeAndReport(cfg: ControlFlowGraph, reporter: DiagnosticReporter) {
        for (node in cfg.nodes) {
            if (node is FunctionCallNode && node.fir.arguments.any { it is FirThisReceiverExpression }) {
                reporter.report(node.fir.source)
            }
        }
    }


}