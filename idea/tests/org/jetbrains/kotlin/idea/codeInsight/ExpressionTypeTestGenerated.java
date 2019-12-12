/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/testData/codeInsight/expressionType")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class ExpressionTypeTestGenerated extends AbstractExpressionTypeTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInExpressionType() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/codeInsight/expressionType"), Pattern.compile("^(.+)\\.kt$"), true);
    }

    @TestMetadata("AnonymousObject.kt")
    public void testAnonymousObject() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/AnonymousObject.kt");
    }

    @TestMetadata("ArgumentName.kt")
    public void testArgumentName() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/ArgumentName.kt");
    }

    @TestMetadata("BlockBodyFunction.kt")
    public void testBlockBodyFunction() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/BlockBodyFunction.kt");
    }

    @TestMetadata("IfAsExpression.kt")
    public void testIfAsExpression() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/IfAsExpression.kt");
    }

    @TestMetadata("IfAsExpressionInsideBlock.kt")
    public void testIfAsExpressionInsideBlock() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/IfAsExpressionInsideBlock.kt");
    }

    @TestMetadata("Kt11601.kt")
    public void testKt11601() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/Kt11601.kt");
    }

    @TestMetadata("Lambda.kt")
    public void testLambda() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/Lambda.kt");
    }

    @TestMetadata("MethodName.kt")
    public void testMethodName() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/MethodName.kt");
    }

    @TestMetadata("MethodReference.kt")
    public void testMethodReference() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/MethodReference.kt");
    }

    @TestMetadata("MultiDeclaration.kt")
    public void testMultiDeclaration() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/MultiDeclaration.kt");
    }

    @TestMetadata("MultiDeclarationInLambda.kt")
    public void testMultiDeclarationInLambda() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/MultiDeclarationInLambda.kt");
    }

    @TestMetadata("MultiDeclarationInLoop.kt")
    public void testMultiDeclarationInLoop() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/MultiDeclarationInLoop.kt");
    }

    @TestMetadata("PropertyAccessor.kt")
    public void testPropertyAccessor() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/PropertyAccessor.kt");
    }

    @TestMetadata("SmartCast.kt")
    public void testSmartCast() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/SmartCast.kt");
    }

    @TestMetadata("SoftSmartCast.kt")
    public void testSoftSmartCast() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/SoftSmartCast.kt");
    }

    @TestMetadata("SoftSmartCastMultipleTypes.kt")
    public void testSoftSmartCastMultipleTypes() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/SoftSmartCastMultipleTypes.kt");
    }

    @TestMetadata("ThisInLambda.kt")
    public void testThisInLambda() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/ThisInLambda.kt");
    }

    @TestMetadata("typeOfLambda.kt")
    public void testTypeOfLambda() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/typeOfLambda.kt");
    }

    @TestMetadata("VariableDeclaration.kt")
    public void testVariableDeclaration() throws Exception {
        runTest("idea/testData/codeInsight/expressionType/VariableDeclaration.kt");
    }
}
