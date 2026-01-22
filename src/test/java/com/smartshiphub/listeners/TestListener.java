package com.smartshiphub.listeners;

import org.testng.*;
import com.aventstack.extentreports.*;
import com.smartshiphub.reports.ExtentManager;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    @Override
    public void onTestStart(ITestResult result) {

        String testName;

        Object[] params = result.getParameters();

        if (params != null && params.length > 0) {
            // DataProvider-based test (LoginTest)
            testName = params[0].toString();
        } else {
            // Normal test (VesselConnectivity)
            testName = result.getMethod().getMethodName();
        }

        test.set(extent.createTest(testName));
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
