/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration.leak

import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.name.FqName
import java.io.File
import java.io.FileOutputStream
import kotlin.system.measureNanoTime
import kotlin.time.ExperimentalTime

object LeakingThisChecker : FirDeclarationChecker<FirRegularClass>() {

    private var klassNum = 0
    private var superTypeNum = 0L
    private var resolvedCalls = 0L

    private var measures = mutableMapOf<Long, Measure>()
    private var totalCalls = 0L
    private var totalSuperTypes = 0L


    private class Measure(t: Long) {
        var n = 1
        var maxResolveTime = t
        var meanResolveTime = t
        override fun toString(): String {
            return "n=$n, maxResolveTime=$maxResolveTime, meanResolveTime=$meanResolveTime"
        }

        fun updateByTime(time: Long) {
            n++
            if (maxResolveTime < time) {
                maxResolveTime = time
            }
            meanResolveTime = if (n == 1) time else (meanResolveTime * (n - 1) / n) + time / n

        }
    }

    val stream = FileOutputStream(File("log.txt"), true)

    @OptIn(ExperimentalTime::class)
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        var fqName: FqName? = null
        val time = measureNanoTime {
            val maxCallResolved = 100
            val maxSuperTypesResolved = 100
            val classInitContext = createClassInitContext(declaration)
            fqName = classInitContext.classId.relativeClassName
            val analyzer = InitContextAnalyzer(context.session, classInitContext, reporter, maxCallResolved, maxSuperTypesResolved)
            analyzer.analyze()

            klassNum += 1
            superTypeNum = analyzer.superTypesResolved.toLong()

            resolvedCalls = analyzer.callResolved.toLong()

        }
        if (measures[superTypeNum] != null) {
            measures[superTypeNum]?.updateByTime(time)
        } else {
            measures[superTypeNum] = Measure(time)
        }

        totalCalls += resolvedCalls
        totalSuperTypes += superTypeNum
        //Name, time per class, superTypeNum, resolvedCalls

        stream.bufferedWriter().use { writer ->
            writer.write("$fqName, $time, $superTypeNum, $resolvedCalls \n")
        }

    }

}

