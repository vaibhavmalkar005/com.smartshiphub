package com.smartshiphub.reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class VesselExtentManager {

    private static ExtentReports vesselExtent;

    private static final String TIMESTAMP =
            new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

    public static synchronized ExtentReports getInstance() {

        if (vesselExtent == null) {

            ExtentSparkReporter spark =
                new ExtentSparkReporter(
                    "reports/Vessel_Connectivity_Report_" + TIMESTAMP + ".html"
                );

            spark.config().setReportName("Vessel Connectivity Report");
            spark.config().setDocumentTitle("Vessel Connectivity Status");

            vesselExtent = new ExtentReports();
            vesselExtent.attachReporter(spark);
        }
        return vesselExtent;
    }
}
