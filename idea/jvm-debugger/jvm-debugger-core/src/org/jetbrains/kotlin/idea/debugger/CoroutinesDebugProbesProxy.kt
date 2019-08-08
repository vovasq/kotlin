/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.idea.debugger

import com.sun.jdi.*
import com.sun.tools.jdi.StringReferenceImpl
import javaslang.control.Either
import org.jetbrains.kotlin.idea.debugger.evaluate.ExecutionContext

// TODO may be make vals top level
object CoroutinesDebugProbesProxy {
    private const val DEBUG_PACKAGE = "kotlinx.coroutines.debug"

    fun install(context: ExecutionContext) {
        val debugProbes = context.findClass("$DEBUG_PACKAGE.DebugProbes") as ClassType
        val instance = with(debugProbes) { getValue(fieldByName("INSTANCE")) as ObjectReference }
        val install = debugProbes.concreteMethodByName("install", "()V")
        context.invokeMethod(instance, install, emptyList())
    }

    fun uninstall(context: ExecutionContext) {
        val debugProbes = context.findClass("$DEBUG_PACKAGE.DebugProbes") as ClassType
        val instance = with(debugProbes) { getValue(fieldByName("INSTANCE")) as ObjectReference }
        val uninstall = debugProbes.concreteMethodByName("uninstall", "()V")
        context.invokeMethod(instance, uninstall, emptyList())
    }

    /**
     * Invokes DebugProbes from debugged process's classpath and returns states of coroutines
     * Should be invoked on debugger manager thread
     */
    fun dumpCoroutines(context: ExecutionContext): Either<Throwable, List<CoroutineState>> {
        try {
            // kotlinx.coroutines.debug.DebugProbes instance and methods
            val debugProbes = context.findClass("$DEBUG_PACKAGE.DebugProbes") as ClassType
            val probesImplType = context.findClass("$DEBUG_PACKAGE.internal.DebugProbesImpl") as ClassType
            val debugProbesImpl = with(probesImplType) { getValue(fieldByName("INSTANCE")) as ObjectReference }
            val enhanceStackTraceWithThreadDump =
                probesImplType.methodsByName("enhanceStackTraceWithThreadDump").single()
            val dumpMethod = debugProbes.concreteMethodByName("dumpCoroutinesInfo", "()Ljava/util/List;")
            val instance = with(debugProbes) { getValue(fieldByName("INSTANCE")) as ObjectReference }

            // CoroutineInfo
            val info = context.findClass("$DEBUG_PACKAGE.CoroutineInfo") as ClassType
            val getState = info.methodsByName("getState").single()
            val getContext = info.methodsByName("getContext").single()
            val idField = info.fieldByName("sequenceNumber")
            val lastObservedStackTrace = info.methodsByName("lastObservedStackTrace").single()
            val coroutineContext = context.findClass("kotlin.coroutines.CoroutineContext") as InterfaceType
            val getContextElement = coroutineContext.methodsByName("get").single()
            val coroutineName = context.findClass("kotlinx.coroutines.CoroutineName") as ClassType
            val getName = coroutineName.methodsByName("getName").single()
            val nameKey = coroutineName.getValue(coroutineName.fieldByName("Key")) as ObjectReference
            val toString = (context.findClass("java.lang.Object") as ClassType)
                .methodsByName("toString").single()

            val threadRef = info.fieldByName("lastObservedThread")
            val continuation = info.fieldByName("lastObservedFrame")

            // get dump
            val infoList = context.invokeMethod(instance, dumpMethod, emptyList()) as ObjectReference

            // Methods for list
            val listType = context.findClass("java.util.List") as InterfaceType
            val getSize = listType.methodsByName("size").single()
            val getElement = listType.methodsByName("get").single()
            val size = (context.invokeMethod(infoList, getSize, emptyList()) as IntegerValue).value()
            val element = context.findClass("java.lang.StackTraceElement") as ClassType

            return Either.right(List(size) {
                val index = context.vm.mirrorOf(it)
                val elem = context.invokeMethod(infoList, getElement, listOf(index)) as ObjectReference
                val name = getName(context, elem, getContext, getContextElement, nameKey, getName, idField)
                val state = getState(context, elem, getState, toString)
                val thread = getLastObservedThread(elem, threadRef)
                CoroutineState(
                    name, state, thread, getStackTrace(
                        elem,
                        lastObservedStackTrace,
                        getSize,
                        getElement,
                        debugProbesImpl,
                        enhanceStackTraceWithThreadDump,
                        element,
                        context
                    ), elem.getValue(continuation) as? ObjectReference
                )
            })
        } catch (e: Throwable) {
            return Either.left(e)
        }
    }

    private fun getName(
        context: ExecutionContext, // Execution context to invoke methods
        info: ObjectReference, // CoroutineInfo instance
        getContext: Method, // CoroutineInfo.getContext()
        getContextElement: Method, // CoroutineContext.get(Key)
        nameKey: ObjectReference, // CoroutineName companion object
        getName: Method, // CoroutineName.getName()
        idField: Field // CoroutineId.idField()
    ): String {
        // equals to `coroutineInfo.context.get(CoroutineName).name`
        val coroutineContextInst = context.invokeMethod(info, getContext, emptyList()) as ObjectReference
        val coroutineName = context.invokeMethod(
            coroutineContextInst,
            getContextElement, listOf(nameKey)
        ) as? ObjectReference
        // If the coroutine doesn't have a given name, CoroutineContext.get(CoroutineName) returns null
        val name = if (coroutineName != null) (context.invokeMethod(
            coroutineName,
            getName, emptyList()
        ) as StringReferenceImpl).value() else "coroutine"
        val id = (info.getValue(idField) as LongValue).value()
        return "$name#$id"
    }

    private fun getState(
        context: ExecutionContext, // Execution context to invoke methods
        info: ObjectReference, // CoroutineInfo instance
        getState: Method, // CoroutineInfo.state field
        toString: Method // CoroutineInfo.State.toString()
    ): String {
        //  equals to stringState = coroutineInfo.state.toString()
        val state = context.invokeMethod(info, getState, emptyList()) as ObjectReference
        return (context.invokeMethod(state, toString, emptyList()) as StringReferenceImpl).value()
    }

    private fun getLastObservedThread(
        info: ObjectReference, // CoroutineInfo instance
        threadRef: Field // reference to lastObservedThread
    ): ThreadReference? = info.getValue(threadRef) as ThreadReference?

    /**
     * Returns list of stackTraceElements for the given CoroutineInfo's [ObjectReference]
     */
    private fun getStackTrace(
        info: ObjectReference,
        lastObservedStackTrace: Method,
        getSize: Method,
        getElement: Method,
        debugProbesImpl: ObjectReference,
        enhanceStackTraceWithThreadDump: Method,
        element: ClassType,
        context: ExecutionContext
    ): List<StackTraceElement> {
        val frameList = context.invokeMethod(info, lastObservedStackTrace, emptyList()) as ObjectReference
        val mergedFrameList = context.invokeMethod(
            debugProbesImpl,
            enhanceStackTraceWithThreadDump, listOf(info, frameList)
        ) as ObjectReference
        val size = (context.invokeMethod(mergedFrameList, getSize, emptyList()) as IntegerValue).value()
        val methodName = element.fieldByName("methodName")
        val className = element.fieldByName("declaringClass")
        val fileName = element.fieldByName("fileName")
        val line = element.fieldByName("lineNumber")

        val list = ArrayList<StackTraceElement>()
        for (it in size - 1 downTo 0) {
            val frame = context.invokeMethod(
                mergedFrameList, getElement,
                listOf(context.vm.virtualMachine.mirrorOf(it))
            ) as ObjectReference
            val clazz = (frame.getValue(className) as StringReference).value()

            if (clazz.contains(DEBUG_PACKAGE)) break // cut off debug intrinsic stacktrace
            list.add(
                0, // add in the beginning
                StackTraceElement(
                    clazz,
                    (frame.getValue(methodName) as StringReference).value(),
                    (frame.getValue(fileName) as StringReference?)?.value(),
                    (frame.getValue(line) as IntegerValue).value()
                )
            )
        }
        return list
    }
}