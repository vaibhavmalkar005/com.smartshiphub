package com.smartshiphub.tests.dashboardPage;

import java.time.*;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.smartshiphub.base.BaseTest;
import com.smartshiphub.dataprovider.EnvironmentDataProvider;
import com.smartshiphub.listeners.TestListener;
import com.smartshiphub.pages.DashboardPage.DashboardPage;
import com.smartshiphub.pages.LoginPage.LoginPage;
import com.smartshiphub.utils.ConfigReader;
import com.smartshiphub.utils.LoginHelper;

@Listeners(TestListener.class)
public class VesselConnectivity extends BaseTest {

    @Test(
        dataProvider = "instanceProvider",
        dataProviderClass = EnvironmentDataProvider.class,
        groups = {"sanity", "smoke"}
    )
    public void verifyVesselConnectivityForAllVessels(String instance) throws Exception {

        launchApplication(instance);

        LoginPage login = new LoginPage(driver);
        String[] creds = LoginHelper.getValidLoginFromExcel();
        login.loginIfRequired(creds[0], creds[1]);

        DashboardPage dashboard = new DashboardPage(driver);
        int thresholdMinutes = ConfigReader.getVesselOnlineThresholdMinutes();

        int vesselCount = dashboard.getVesselCount();
        Assert.assertTrue(vesselCount > 0, "No vessels found");

        for (int i = 0; i < vesselCount; i++) {

            String vesselName = dashboard.selectVesselByIndex(i);
            Reporter.log("========================================", true);
            Reporter.log("Checking Vessel: " + vesselName, true);

            LocalDateTime nowUTC = LocalDateTime.now(ZoneOffset.UTC);
            Reporter.log("Current UTC Time      : " + nowUTC, true);

            /* ---------------- PRIORITY 1 ---------------- */
            String lastUpdatedText = dashboard.waitForLastUpdatedOrNA(20);
            Reporter.log("Last Updated Raw Text : " + lastUpdatedText, true);

            // ✅ OFFLINE → DO NOT PARSE
            if ("NA".equalsIgnoreCase(lastUpdatedText)) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: LastUpdated is NA – vessel offline)",
                    true
                );
                continue;
            }

            LocalDateTime lastUpdatedUTC = parseDateTimeSafely(lastUpdatedText);

            // ✅ extra safety (corrupt value etc.)
            if (lastUpdatedUTC == null) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: LastUpdated NA)",
                    true
                );
                continue;
            }

            long diffLastUpdated =
                Duration.between(lastUpdatedUTC, nowUTC).toMinutes();

            Reporter.log("Parsed LastUpdated UTC: " + lastUpdatedUTC, true);
            Reporter.log("LastUpdated Diff (min): " + diffLastUpdated, true);

            /* ---------------- PRIORITY 2 ---------------- */
            String tooltipRaw = dashboard.getTooltipTimeFromGraph();
            Reporter.log("Graph Tooltip Time   : " + tooltipRaw, true);

            if (tooltipRaw == null || tooltipRaw.isEmpty()) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: Tooltip time missing)",
                    true
                );
                continue; // ✅ business logic untouched
            }

            LocalDateTime tooltipUTC = parseTooltipTimeSafely(tooltipRaw);

            if (tooltipUTC == null) {
                Reporter.log("DECISION → OFFLINE (Reason: Tooltip time invalid format)", true);
                continue;
            }


            long diffTooltip =
                Duration.between(tooltipUTC, nowUTC).toMinutes();

            Reporter.log("Parsed Tooltip UTC   : " + tooltipUTC, true);
            Reporter.log("Tooltip Diff (min)   : " + diffTooltip, true);

            /* ---------------- FINAL DECISION ---------------- */
            if (diffLastUpdated > thresholdMinutes || diffTooltip > thresholdMinutes) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: Time diff exceeded threshold "
                    + thresholdMinutes + " min)",
                    true
                );
            } else {
                Reporter.log(
                    "DECISION → ONLINE (Reason: Both times within "
                    + thresholdMinutes + " min)",
                    true
                );
            }
        }
    }

    /* ================= SAFE DATE PARSER ================= */

    private LocalDateTime parseDateTimeSafely(String raw) {

        if (raw == null) return null;

        raw = raw.replaceFirst("^:\\s*", "").trim();

        if (raw.isEmpty() || raw.equalsIgnoreCase("NA")) {
            return null;
        }

        DateTimeFormatter ddMMyyyy =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        DateTimeFormatter yyyyMMdd =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            return LocalDateTime.parse(raw, ddMMyyyy);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(raw, yyyyMMdd);
            } catch (Exception ex) {
                return null;
            }
        }
    }
    private LocalDateTime parseTooltipTimeSafely(String raw) {

        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        raw = raw.trim();

        try {
            // Case 1: yyyy-MM-dd HH:mm:ss
            return LocalDateTime.parse(
                    raw,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ignore) { }

        try {
            // Case 2: yyyy-MM-dd HHmmss  → normalize to HH:mm:ss
            // Example: 2026-02-01 111850 → 2026-02-01 11:18:50
            String date = raw.substring(0, 10);
            String time = raw.substring(11);

            if (time.length() == 6) {
                String normalizedTime =
                        time.substring(0, 2) + ":" +
                        time.substring(2, 4) + ":" +
                        time.substring(4, 6);

                return LocalDateTime.parse(
                        date + " " + normalizedTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception ignore) { }

        return null; // invalid → business logic decides OFFLINE
    }

}
