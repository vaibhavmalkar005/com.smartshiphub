package com.smartshiphub.listeners;

import org.testng.*;

import com.aventstack.extentreports.*;
import com.smartshiphub.reports.ExtentManager;
import com.smartshiphub.utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    private boolean isVesselConnectivityTest(ITestResult result) {
        return result.getTestClass()
                     .getName()
                     .contains("VesselConnectivity");
    }

    @Override
    public void onTestStart(ITestResult result) {

        // 🚫 SKIP vessel connectivity for automation report
        if (isVesselConnectivityTest(result)) {
            return;
        }

        String testName;
        Object[] params = result.getParameters();

        if (params != null && params.length > 0) {
            testName = result.getMethod().getMethodName()
                       + " - " + params[0];
        } else {
            testName = result.getMethod().getMethodName();
        }

        test.set(extent.createTest(testName));
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        if (isVesselConnectivityTest(result)) {
            return;
        }

        test.get().pass("Test Passed");
    }

    @Override
public void onTestFailure(ITestResult result) {

    if (isVesselConnectivityTest(result)) {
        return;
    }

    String testName = test.get().getModel().getName();

    /* ================= MODIFIED: Better Screenshot Naming ================= */
    String screenshotPath =
            ScreenshotUtils.capture(
                testName.replaceAll("[^a-zA-Z0-9]", "_"));

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
