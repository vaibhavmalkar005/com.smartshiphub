package com.smartshiphub.listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.smartshiphub.reports.VesselReportUtil;

public class VesselSummarySuiteListener implements ISuiteListener {

    @Override
    public void onFinish(ISuite suite) {

        /* ✅ FINAL FLUSH – RUNS ONCE AFTER ALL TESTS */
        VesselReportUtil.flush();
    }
}
