package com.smartshiphub.listeners;

import org.testng.*;

import com.aventstack.extentreports.*;
import com.smartshiphub.reports.ExtentManager;
import com.smartshiphub.utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {

        String testName;
        Object[] params = result.getParameters();

        if (params != null && params.length > 0) {
            testName = params[0].toString();
        } else {
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
        String testName = test.get().getModel().getName();
        String screenshotPath = ScreenshotUtils.capture(testName);

        test.get().fail(result.getThrowable());

        if (screenshotPath != null) {
            try {
                test.get().addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
