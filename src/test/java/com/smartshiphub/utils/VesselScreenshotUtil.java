package com.smartshiphub.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.smartshiphub.factory.DriverFactory;

public class VesselScreenshotUtil {

    public static String captureBase64() {
        try {
            TakesScreenshot ts =
                (TakesScreenshot) DriverFactory.getDriver();

            return ts.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            return null;
        }
    }
}
