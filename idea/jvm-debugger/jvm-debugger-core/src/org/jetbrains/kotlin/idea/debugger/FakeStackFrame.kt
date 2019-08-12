/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger

import com.intellij.debugger.engine.JavaStackFrame
import com.intellij.debugger.jdi.StackFrameProxyImpl
import com.intellij.debugger.ui.impl.watch.MethodsTracker
import com.intellij.debugger.ui.impl.watch.StackFrameDescriptorImpl
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueChildrenList

/**
 * Puts the frameProxy into JavaStackFrame just to instantiate. FakeStackFrame provides it's own data for variables view.
 */
class FakeStackFrame(frame: StackFrameProxyImpl, private val vars: List<XNamedValue>, private val position: XSourcePosition) :
    JavaStackFrame(StackFrameDescriptorImpl(frame, MethodsTracker()), true) {

    override fun computeChildren(node: XCompositeNode) {
        val list = XValueChildrenList()
        vars.forEach { list.add(it) }
        node.addChildren(list, true)
    }

    override fun getSourcePosition(): XSourcePosition? {
        return position
    }
}