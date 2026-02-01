package com.smartshiphub.tests.dashboardPage;

import java.time.*;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.smartshiphub.base.BaseTest;
import com.smartshiphub.listeners.TestListener;
import com.smartshiphub.pages.DashboardPage.DashboardPage;
import com.smartshiphub.pages.LoginPage.LoginPage;
import com.smartshiphub.utils.ConfigReader;
import com.smartshiphub.utils.LoginHelper;

@Listeners(TestListener.class)
public class VesselConnectivity extends BaseTest {

    @Test(groups = {"sanity", "smoke"})
    public void verifyVesselConnectivityForAllVessels() throws Exception {

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

            if ("NA".equalsIgnoreCase(lastUpdatedText)) {
                Reporter.log(
                        "DECISION → OFFLINE (Reason: LastUpdated stayed NA for 20 sec)",
                        true);
                continue;
            }

            LocalDateTime lastUpdatedUTC =
                    LocalDateTime.parse(
                            lastUpdatedText.replaceFirst("^:\\s*", "").trim(),
                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

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
                        true);
                continue;
            }

            LocalDateTime tooltipUTC =
                    LocalDateTime.parse(
                            tooltipRaw,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            long diffTooltip =
                    Duration.between(tooltipUTC, nowUTC).toMinutes();

            Reporter.log("Parsed Tooltip UTC   : " + tooltipUTC, true);
            Reporter.log("Tooltip Diff (min)   : " + diffTooltip, true);

            /* ---------------- FINAL DECISION ---------------- */
            if (diffLastUpdated > thresholdMinutes
                    || diffTooltip > thresholdMinutes) {

                Reporter.log(
                        "DECISION → OFFLINE (Reason: Time diff exceeded threshold "
                                + thresholdMinutes + " min)",
                        true);
            } else {
                Reporter.log(
                        "DECISION → ONLINE (Reason: Both times within "
                                + thresholdMinutes + " min)",
                        true);
            }
        }
    }
}
