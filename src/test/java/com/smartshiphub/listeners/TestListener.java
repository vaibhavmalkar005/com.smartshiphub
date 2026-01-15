package com.smartshiphub.listeners;

import org.testng.*;
import com.aventstack.extentreports.*;
import com.smartshiphub.reports.ExtentManager;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        // Only test case name (Excel column 1)
        String testCaseName = result.getParameters()[0].toString();
        test.set(extent.createTest(testCaseName));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
