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
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ConstExpressionNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionCallNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.QualifiedAccessNode
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol


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

    private fun collectDataForSimpleClassAnalysis(classDeclaration: FirRegularClass): BaseClassMembersContext =
        BaseClassMembersContext(
            classDeclaration
        )

    private fun runCheck(classMembersContext: BaseClassMembersContext, reporter: DiagnosticReporter) {
        if (classMembersContext.isClassNotRelevantForChecker)
            return
        val initializedProps = mutableListOf<FirVariableSymbol<*>>()

        for (initState in classMembersContext.classInitStates.asReversed()) {
            when (initState.state) {
                InitState.ASSINGMENT_OR_INITIALIZER -> {
                    if (initState.affectingNodes.all {
                            it is ConstExpressionNode
                                    || (it is FunctionCallNode
                                    && !it.fir.calleeReference.isMemberOfTheClass(classMembersContext.classDeclaration.symbol.classId))
                                    || (it is QualifiedAccessNode
                                    && it.fir.calleeReference.isMemberOfTheClass(classMembersContext.classDeclaration.symbol.classId)
                                    && initializedProps.contains(it.fir.calleeReference.resolvedSymbolAsProperty!!))
                        })
                        initializedProps.add(initState.accessedProperties[0]) // TODO
                }
                InitState.QUAILIFIED_ACCESS -> {
                    if (!initState.accessedProperties.all {
                            it in initializedProps
                        })
                        reporter.report(initState.cfgNode.fir.source)
                }
                else -> {

                }
            }
        }


    }

    private fun checkAffectingNodes(
        initState: InitializeContextNode,
        initializedProps: MutableList<FirVariableSymbol<*>>, classDeclaration: FirRegularClass
    ) {
    }


    private fun checkConstructor(
        constructorDecl: FirConstructor,
        classMembersContext: BaseClassMembersContext,
        reporter: DiagnosticReporter
    ) {
        buildLatticeAndComputeFixedPoint(
            classMembersContext
        )
        reporter.report(constructorDecl.source)
    }

    private fun buildLatticeAndComputeFixedPoint(
        classMembersContext: BaseClassMembersContext,
    ) {
        if (!classMembersContext.isCfgAvailable)
            return

    }

    private fun DiagnosticReporter.report(source: FirSourceElement?) {
        source?.let { report(FirErrors.LEAKING_THIS_IN_CONSTRUCTOR.on(it, "Possible leaking this in constructor")) }
    }

}

