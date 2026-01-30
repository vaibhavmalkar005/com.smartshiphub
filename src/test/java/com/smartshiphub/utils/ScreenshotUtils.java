package com.smartshiphub.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.*;

import com.smartshiphub.factory.DriverFactory;

public class ScreenshotUtils {

    public static String capture(String testName) {
        try {
            String timestamp =
                new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

            TakesScreenshot ts =
                (TakesScreenshot) DriverFactory.getDriver();

            File src = ts.getScreenshotAs(OutputType.FILE);
            String path =
                "./screenshots/" + testName + "_" + timestamp + ".png";

            File dest = new File(path);
            dest.getParentFile().mkdirs();
            src.renameTo(dest);

            return path;
        } catch (Exception e) {
            return null;
        }
    }
}
