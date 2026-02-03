package com.smartshiphub.tests.dashboardPage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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

            Reporter.log("\n========================================", true);
            Reporter.log("Checking Vessel: " + vesselName, true);

            LocalDateTime nowUTC = LocalDateTime.now(ZoneOffset.UTC);
            Reporter.log("Current UTC Time      : " + nowUTC, true);

            /* ================= PRIORITY 1 ================= */

            String lastUpdatedText = dashboard.waitForLastUpdatedOrNA(20);
            Reporter.log("Last Updated Raw Text : " + lastUpdatedText, true);

            if ("NA".equalsIgnoreCase(lastUpdatedText)) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: LastUpdated is NA – no backend data)",
                    true
                );
                continue;
            }

            LocalDateTime lastUpdatedUTC = parseDateTimeSafely(lastUpdatedText);
            if (lastUpdatedUTC == null) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: LastUpdated invalid format)",
                    true
                );
                continue;
            }

            long diffLastUpdated =
                Duration.between(lastUpdatedUTC, nowUTC).toMinutes();

            Reporter.log("Parsed LastUpdated UTC: " + lastUpdatedUTC, true);
            Reporter.log("LastUpdated Diff (min): " + diffLastUpdated, true);

            /* ================= PRIORITY 2 ================= */

            if (diffLastUpdated > thresholdMinutes) {
                Reporter.log(
                    "Skipping tooltip → vessel appears idle (LastUpdated exceeds threshold)",
                    true
                );
                Reporter.log(
                    "DECISION → OFFLINE (Reason: LastUpdated exceeded threshold)",
                    true
                );
                continue;
            }

            /* ================= TOOLTIP (NO LOGIC CHANGE) ================= */

            String tooltipRaw = dashboard.getTooltipTimeFromGraph();
            Reporter.log("Graph Tooltip Time   : " + tooltipRaw, true);

            // 🔍 DIAGNOSTIC WAIT ONLY (NO LOGIC CHANGE)
            if (tooltipRaw == null || tooltipRaw.trim().isEmpty()) {
                Reporter.log(
                    "NOTE → Tooltip missing, graph may still be rendering",
                    true
                );
                Reporter.log(
                    "DEBUG → Waiting extra 3 seconds and retrying ONCE",
                    true
                );

                Thread.sleep(3000);

                tooltipRaw = dashboard.getTooltipTimeFromGraph();
                Reporter.log("Graph Tooltip Retry : " + tooltipRaw, true);
            }

            if (tooltipRaw == null || tooltipRaw.trim().isEmpty()) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: Tooltip missing)",
                    true
                );
                continue;
            }

            LocalDateTime tooltipUTC = parseTooltipTimeSafely(tooltipRaw);
            if (tooltipUTC == null) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: Tooltip invalid format)",
                    true
                );
                continue;
            }

            long diffTooltip =
                Duration.between(tooltipUTC, nowUTC).toMinutes();

            Reporter.log("Parsed Tooltip UTC   : " + tooltipUTC, true);
            Reporter.log("Tooltip Diff (min)   : " + diffTooltip, true);

            /* ================= FINAL DECISION ================= */

            if (diffLastUpdated > thresholdMinutes || diffTooltip > thresholdMinutes) {
                Reporter.log(
                    "DECISION → OFFLINE (Reason: Time difference exceeded "
                    + thresholdMinutes + " minutes)",
                    true
                );
            } else {
                Reporter.log(
                    "DECISION → ONLINE (Reason: Both timestamps within "
                    + thresholdMinutes + " minutes)",
                    true
                );
            }
        }
    }

    /* ================= SAFE LAST UPDATED PARSER ================= */

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

    /* ================= SAFE TOOLTIP PARSER ================= */

    private LocalDateTime parseTooltipTimeSafely(String raw) {

        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        raw = raw.trim();

        try {
            return LocalDateTime.parse(
                raw,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
        } catch (Exception ignore) { }

        try {
            String date = raw.substring(0, 10);
            String time = raw.substring(11);

            if (time.length() == 6) {
                String normalized =
                    time.substring(0, 2) + ":" +
                    time.substring(2, 4) + ":" +
                    time.substring(4, 6);

                return LocalDateTime.parse(
                    date + " " + normalized,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );
            }
        } catch (Exception ignore) { }

        return null;
    }
}
