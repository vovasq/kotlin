/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/testData/ir/irCfg")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class IrCfgTestCaseGenerated extends AbstractIrCfgTestCase {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInIrCfg() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/ir/irCfg"), Pattern.compile("^(.+)\\.kt$"), true);
    }

    @TestMetadata("expressionFun.kt")
    public void testExpressionFun() throws Exception {
        runTest("compiler/testData/ir/irCfg/expressionFun.kt");
    }

    @TestMetadata("expressionUnit.kt")
    public void testExpressionUnit() throws Exception {
        runTest("compiler/testData/ir/irCfg/expressionUnit.kt");
    }

    @TestMetadata("returnUnit.kt")
    public void testReturnUnit() throws Exception {
        runTest("compiler/testData/ir/irCfg/returnUnit.kt");
    }

    @TestMetadata("sequentialFun.kt")
    public void testSequentialFun() throws Exception {
        runTest("compiler/testData/ir/irCfg/sequentialFun.kt");
    }

    @TestMetadata("simpleFun.kt")
    public void testSimpleFun() throws Exception {
        runTest("compiler/testData/ir/irCfg/simpleFun.kt");
    }

    @TestMetadata("simpleReturn.kt")
    public void testSimpleReturn() throws Exception {
        runTest("compiler/testData/ir/irCfg/simpleReturn.kt");
    }

    @TestMetadata("compiler/testData/ir/irCfg/loop")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Loop extends AbstractIrCfgTestCase {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInLoop() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/ir/irCfg/loop"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("digitCount.kt")
        public void testDigitCount() throws Exception {
            runTest("compiler/testData/ir/irCfg/loop/digitCount.kt");
        }

        @TestMetadata("factorial.kt")
        public void testFactorial() throws Exception {
            runTest("compiler/testData/ir/irCfg/loop/factorial.kt");
        }

        @TestMetadata("isPerfect.kt")
        public void testIsPerfect() throws Exception {
            runTest("compiler/testData/ir/irCfg/loop/isPerfect.kt");
        }
    }

    @TestMetadata("compiler/testData/ir/irCfg/when")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class When extends AbstractIrCfgTestCase {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInWhen() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/ir/irCfg/when"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("cascadeIf.kt")
        public void testCascadeIf() throws Exception {
            runTest("compiler/testData/ir/irCfg/when/cascadeIf.kt");
        }

        @TestMetadata("emptyWhen.kt")
        public void testEmptyWhen() throws Exception {
            runTest("compiler/testData/ir/irCfg/when/emptyWhen.kt");
        }

        @TestMetadata("expressionIf.kt")
        public void testExpressionIf() throws Exception {
            runTest("compiler/testData/ir/irCfg/when/expressionIf.kt");
        }

        @TestMetadata("ifChain.kt")
        public void testIfChain() throws Exception {
            runTest("compiler/testData/ir/irCfg/when/ifChain.kt");
        }

        @TestMetadata("whenReturn.kt")
        public void testWhenReturn() throws Exception {
            runTest("compiler/testData/ir/irCfg/when/whenReturn.kt");
        }
    }
}
