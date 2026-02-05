package com.smartshiphub.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.smartshiphub.utils.VesselScreenshotUtil;
import com.smartshiphub.vesselreport.VesselSummaryRenderer;

public class VesselReportUtil {

    private static ExtentReports extent =
            VesselExtentManager.getInstance();

    private static ThreadLocal<ExtentTest> instanceNode =
            new ThreadLocal<>();

    private static ThreadLocal<ExtentTest> vesselNode =
            new ThreadLocal<>();

    /* ================= INSTANCE HEADER ================= */

    public static void createInstanceHeader(String instance) {

        ExtentTest instanceTest =
                extent.createTest("INSTANCE : " + instance.toUpperCase());

        instanceNode.set(instanceTest);
    }

    /* ================= VESSEL SUB HEADER ================= */

    public static void createVesselHeader(String vesselName) {

        ExtentTest vesselTest =
                instanceNode.get().createNode("Vessel : " + vesselName);

        vesselNode.set(vesselTest);
    }

    /* ================= STATUS LOGGING ================= */

    public static void logOnline(String message) {

        vesselNode.get().pass(
            MarkupHelper.createLabel(message, ExtentColor.GREEN)
        );

        attachScreenshot();
    }

    public static void logOffline(String message) {

        vesselNode.get().fail(
            MarkupHelper.createLabel(message, ExtentColor.RED)
        );

        attachScreenshot();
    }

    /* ================= INLINE SCREENSHOT ================= */

    private static void attachScreenshot() {

        String base64 = VesselScreenshotUtil.captureBase64();

        if (base64 != null) {
            vesselNode.get().addScreenCaptureFromBase64String(base64);
        }
    }

    /* ================= FLUSH ================= */

    public static void flush() {
    	 VesselSummaryRenderer.render(extent);
        extent.flush();
    }
}
