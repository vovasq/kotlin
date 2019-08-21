/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.coroutines

import com.intellij.debugger.engine.JavaValue
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.util.io.FileUtil
import com.sun.jdi.ClassType
import org.jetbrains.kotlin.idea.debugger.KotlinCoroutinesAsyncStackTraceProvider
import org.jetbrains.kotlin.idea.debugger.KotlinDebuggerTestBase
import org.jetbrains.kotlin.idea.debugger.evaluate.ExecutionContext
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import java.lang.AssertionError

abstract class AbstractCoroutineDumpTest : KotlinDebuggerTestBase() {


    protected fun doTest(path: String) {
        val fileText = FileUtil.loadFile(File(path))

        configureSettings(fileText)
        createAdditionalBreakpoints(fileText)
        createDebugProcess(path)

        doOnBreakpoint {
            val evalContext = EvaluationContextImpl(this, frameProxy)
            val execContext = ExecutionContext(evalContext, frameProxy ?: return@doOnBreakpoint)
            val either = CoroutinesDebugProbesProxy.dumpCoroutines(execContext)
            try {
                if (either.isRight)
                    try {
                        val states = either.get()
                        print(stringDump(states, execContext), ProcessOutputTypes.SYSTEM)
                    } catch (ignored: Throwable) {
                        Unit
                    }
                else
                    throw AssertionError("Dump failed", either.left)
            } finally {
                resume(this)
            }
        }
    }

    private fun stringDump(states: List<CoroutineState>, context: ExecutionContext) = buildString {
        states.forEach {
            appendln("\"${it.name}\", state: ${it.state}")
            it.stackTrace.forEach { frame ->
                appendln("\t$frame")
                val continuation = it.getContinuation(frame, context)
                if (continuation != null) {
                    val aMethod = (continuation.type() as ClassType).concreteMethodByName(
                        "getStackTraceElement",
                        "()Ljava/lang/StackTraceElement;"
                    )
                    val debugMetadataKtType = context
                        .findClass("kotlin.coroutines.jvm.internal.DebugMetadataKt") as ClassType

                    with(KotlinCoroutinesAsyncStackTraceProvider()) {
                        KotlinCoroutinesAsyncStackTraceProvider
                            .AsyncStackTraceContext(context, aMethod, debugMetadataKtType)
                            .getSpilledVariables(continuation)
                            ?.forEach { v ->
                                if (v is JavaValue)
                                    appendln("\t\t${v.name}")
                            }
                    }
                }
            }
        }
    }

    override fun createJavaParameters(mainClass: String?): JavaParameters {
        val path = "/Users/aleksandr.prokopyev/.m2/repository/org/jetbrains/kotlinx/kotlinx-coroutines-debug/" +
                "1.3.0-RC-SNAPSHOT/kotlinx-coroutines-debug-1.3.0-RC-SNAPSHOT.jar"
        val jar = PathUtil.getResourcePathForClass(Class.forName("kotlinx.coroutines.CoroutineName"))
        val params = super.createJavaParameters(mainClass)
        params.classPath.add(path)
        params.classPath.add(jar.path)
        params.vmParametersList.add("-javaagent:$path")
        return params
    }

    override fun collectClasspath(): Array<String> {
        val cp = super.collectClasspath()
        val jar = PathUtil.getResourcePathForClass(Class.forName("kotlinx.coroutines.CoroutineName"))
        return arrayOf(*cp, jar.path)
    }
}