/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInsight.FileModificationService
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.idea.quickfix.ChangeCallableReturnTypeFix
import org.jetbrains.kotlin.idea.quickfix.ChangeParameterTypeFix
import org.jetbrains.kotlin.idea.quickfix.ChangeVariableTypeFix
import org.jetbrains.kotlin.idea.quickfix.moveCaretToEnd
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.TypeUtils

class UnsafeNotNullAssertionOnReallyNullableInspection : AbstractKotlinInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return postfixExpressionVisitor(fun(expression) {
            if (expression.operationToken != KtTokens.EXCLEXCL) return
            val context = expression.analyze(BodyResolveMode.PARTIAL_WITH_DIAGNOSTICS)
            if (context.diagnostics.forElement(expression.operationReference)
                    .any { it.factory == Errors.UNNECESSARY_NOT_NULL_ASSERTION }) return

            when (val base = expression.baseExpression) {
                is KtNameReferenceExpression -> {
                    registerFixForNamedReference(base, context, holder, expression)
                }
                is KtCallExpression -> {
                    registerChangeCallableReturnFix(
                        expression,
                        base,
                        context,
                        holder,
                        expression
                    )
                }
                is KtDotQualifiedExpression -> {
                    val selector = base.selectorExpression
                    if (selector is KtNameReferenceExpression)
                        registerFixForNamedReference(selector, context, holder, expression)
                    else registerChangeCallableReturnFix(
                        expression,
                        //TODO: add reference type loop
                        base.selectorExpression as? KtCallExpression ?: return,
                        context,
                        holder,
                        expression
                    )
                }
            }
//            holder.registerProblem(
//                expression,
//                "Unsafe using of '!!' operator",
//                ProblemHighlightType.WEAK_WARNING,
//                AddSaveCallAndElvisFix()
//            )
        })
    }

    private fun registerChangeCallableReturnFix(
        postfixExpression: KtPostfixExpression,
        callExpression: KtCallExpression,
        context: BindingContext,
        holder: ProblemsHolder,
        expression: KtPostfixExpression
    ) {
        val basePsi = callExpression.calleeExpression?.mainReference?.resolve() ?: return
        val fooDescriptor = context[BindingContext.FUNCTION, basePsi] ?: return
        val fix = ChangeCallableReturnTypeFix.OnType(
            callExpression.calleeExpression?.mainReference?.resolve() as? KtFunction ?: return,
            TypeUtils.makeNotNullable(fooDescriptor.returnType ?: return)
        )
        holder.registerProblem(
            postfixExpression.operationReference, inspectionDescription,
            ProblemHighlightType.WEAK_WARNING,
//            IntentionWrapper(fix, postfixExpression.containingFile)
            WrapperWithEmbeddedFixBeforeMainFix(fix, expression, postfixExpression.containingFile)
        )

    }

    private fun registerFixForNamedReference(
        base: KtNameReferenceExpression,
        context: BindingContext,
        holder: ProblemsHolder,
        expression: KtPostfixExpression
    ) {
        when (val basePsi = base.mainReference.resolve()) {
            is KtParameter -> {
                val baseExpressionDescriptor = context[BindingContext.VALUE_PARAMETER, basePsi]
                if (baseExpressionDescriptor is ValueParameterDescriptorImpl) {
                    val fix = ChangeParameterTypeFix(
                        basePsi,
                        TypeUtils.makeNotNullable(baseExpressionDescriptor.returnType)
                    )
                    holder.registerProblem(
                        expression, inspectionDescription,
                        ProblemHighlightType.WEAK_WARNING,
//                        IntentionWrapper(fix, basePsi.containingFile)
                        WrapperWithEmbeddedFixBeforeMainFix(fix, expression, basePsi.containingFile)
                    )
                }
            }
            is KtProperty -> {
                val rawDescriptor = context[BindingContext.REFERENCE_TARGET, base]
                val propertyDescriptor = rawDescriptor as CallableDescriptor
                val declaration = basePsi as KtVariableDeclaration
                val fix = ChangeVariableTypeFix.OnType(
                    declaration,
                    TypeUtils.makeNotNullable(propertyDescriptor.returnType ?: return)
                )
                holder.registerProblem(
                    expression, inspectionDescription,
                    ProblemHighlightType.WEAK_WARNING, IntentionWrapper(fix, declaration.containingFile)
                )
            }
        }

    }

    private class WrapperWithEmbeddedFixBeforeMainFix(
        intention: IntentionAction,
        val postfixExpression: KtPostfixExpression,
        file: PsiFile
    ) : IntentionWrapper(intention, file) {
        override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
//            removeExclExclBefore(project, file, postfixExpression)

            if (!FileModificationService.getInstance().prepareFileForWrite(file)) return
            val expression =
                KtPsiFactory(project).createExpression(postfixExpression.baseExpression!!.text)
            postfixExpression.replace(expression)
            super.invoke(project, editor, file)
        }

//        private fun removeExclExclBefore(
//            project: Project,
//            file: PsiFile?,
//            postfixExpression: KtPostfixExpression
//        ) {
//            if (!FileModificationService.getInstance().prepareFileForWrite(file)) return
//            val expression =
//                KtPsiFactory(project).createExpression(postfixExpression.baseExpression!!.text)
//            postfixExpression.replace(expression)
//        }
    }

    companion object {
        val inspectionDescription: String = "Unsafe using of '!!' operator"
    }
}

private class RemoveExclExclFix : LocalQuickFix {

    override fun getFamilyName(): String = name

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        applyFix(project, descriptor.psiElement as? KtPostfixExpression ?: return)
    }

    private fun applyFix(project: Project, expression: KtPostfixExpression) {
        if (!FileModificationService.getInstance().preparePsiElementForWrite(expression)) return
//        val postfix = expression.receiverExpression as? KtPostfixExpression ?: return
        val editor = expression.findExistingEditor()
        expression.replaced(KtPsiFactory(expression).buildExpression {
            appendExpression(expression.baseExpression)
        }).moveCaretToEnd(editor, project)
    }
}


//TODO:
//private class AddSaveCallAndElvisFix : LocalQuickFix {
//    override fun getName() = "Replace unsafe assertion with safe call and elvis"
//
//    override fun getFamilyName(): String = name
//
//    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
//        applyFix(project, descriptor.psiElement as? KtPostfixExpression ?: return)
//    }
//
//    private fun applyFix(project: Project, expression: KtPostfixExpression) {
//        if (!FileModificationService.getInstance().preparePsiElementForWrite(expression)) return
////        val postfix = expression.receiverExpression as? KtPostfixExpression ?: return
//        val editor = expression.findExistingEditor()
//        expression.replaced(KtPsiFactory(expression).buildExpression {
//            appendExpression(expression.baseExpression)
//            appendFixedText("?.")
//            appendExpression(expression.selectorExpression)
//            appendFixedText("?:")
//        }).moveCaretToEnd(editor, project)
//    }
//}