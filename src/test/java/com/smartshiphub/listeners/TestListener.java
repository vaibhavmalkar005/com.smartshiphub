package com.smartshiphub.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import com.smartshiphub.utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        ScreenshotUtils.capture(result.getName());
    }
}
