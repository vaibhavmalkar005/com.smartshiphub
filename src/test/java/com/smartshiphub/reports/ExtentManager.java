package com.smartshiphub.reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    // Static timestamp (one per execution)
    private static final String TIMESTAMP =
            new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

    public static ExtentReports getInstance() {

        if (extent == null) {

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(
                            "reports/Login_Automation_Report_" + TIMESTAMP + ".html"
                    );

            spark.config().setReportName("Login Automation Report");
            spark.config().setDocumentTitle("SmartShip Test Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }
}
