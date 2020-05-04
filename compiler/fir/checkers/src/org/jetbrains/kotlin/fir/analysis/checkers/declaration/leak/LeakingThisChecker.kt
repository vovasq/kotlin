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
        val initializedProps = mutableSetOf<FirVariableSymbol<*>>()

        for (initContextNode in classMembersContext.classInitContextNodes.asReversed()) {
            when (initContextNode.nodeType) {
                ContextNodeType.ASSINGMENT_OR_INITIALIZER -> {
                    if (initContextNode.affectingNodes.all {
                            it.cfgNode is ConstExpressionNode // TODO: add visitConstNode?
                                    || (it.nodeType == ContextNodeType.UNRESOLVABLE_FUN_CALL)
                                    || (it.nodeType == ContextNodeType.PROPERTY_QUALIFIED_ACCESS
                                    && it.firstAccessedProperty.callableId.classId == classMembersContext.classId
                                    && initializedProps.contains(it.firstAccessedProperty))
                        }) {
                        initContextNode.confirmInitForCandidate()
                        initializedProps.add(initContextNode.initCandidate)
                    }
                }
                ContextNodeType.PROPERTY_QUALIFIED_ACCESS -> {
                    if (initContextNode.accessedProperties.any {
                            it !in initializedProps
                        })
                    // TODO: report p1.length not p1 exactly
                        reporter.report(initContextNode.cfgNode.fir.source)
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

