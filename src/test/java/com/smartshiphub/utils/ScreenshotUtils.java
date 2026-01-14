package com.smartshiphub.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;

import com.smartshiphub.factory.DriverFactory;

public class ScreenshotUtils {

    public static void capture(String testName) {
        try {
            // Generate timestamp
            String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

            TakesScreenshot ts = (TakesScreenshot) DriverFactory.getDriver();
            File src = ts.getScreenshotAs(OutputType.FILE);

            // Screenshot name with timestamp
            File dest = new File("./screenshots/" + testName + "_" + timestamp + ".png");
            dest.getParentFile().mkdirs();

            FileHandler.copy(src, dest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
