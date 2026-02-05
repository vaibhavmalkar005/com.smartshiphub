package com.smartshiphub.reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    
    private static final String TIMESTAMP =
            new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

    public static ExtentReports getInstance() {

        if (extent == null) {

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(
                            "reports/Automation Report " + TIMESTAMP + ".html"
                    );

            spark.config().setReportName("Automation Report");
            spark.config().setDocumentTitle("SmartShip QA Automation Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }
}
