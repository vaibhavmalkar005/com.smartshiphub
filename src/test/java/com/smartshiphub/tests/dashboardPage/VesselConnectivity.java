package com.smartshiphub.tests.dashboardPage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
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

    @Test
    public void verifyVesselConnectivityFromDashboardAndGraph() throws Exception {

        // ---------- Login ----------
        ConfigReader.initProperties();
        LoginPage login = new LoginPage(driver);
        String[] creds = LoginHelper.getValidLoginFromExcel();
        login.login(creds[0], creds[1]);

        Assert.assertTrue(login.isLoginSuccessful(),
                "Login failed, dashboard not loaded");

        // ---------- Dashboard Last Updated ----------
        DashboardPage dashboardPage = new DashboardPage(driver);

        String lastUpdatedText =
                dashboardPage.waitForLastUpdatedDateTime();

        String cleanedDashboardTime =
                lastUpdatedText.replaceFirst("^:\\s*", "");

        DateTimeFormatter dashboardFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        LocalDateTime dashboardUTC =
                LocalDateTime.parse(cleanedDashboardTime, dashboardFormatter);

        System.out.println("Dashboard Last Updated (UTC): " + dashboardUTC);

        // ---------- Tooltip Time ----------
        String tooltipTimeRaw =
                dashboardPage.getTooltipTimeFromGraph();

        LocalDateTime tooltipUTC =
                dashboardPage.parseTooltipTime(tooltipTimeRaw);

        System.out.println("Tooltip Time (UTC): " + tooltipUTC);

     // ---------- Time difference vs Current UTC ----------
        LocalDateTime currentUTC = LocalDateTime.now(ZoneOffset.UTC);

        long dashboardDiffFromNow =
                Duration.between(dashboardUTC, currentUTC).toMinutes();

        long tooltipDiffFromNow =
                Duration.between(tooltipUTC, currentUTC).toMinutes();

        int thresholdMinutes =
                ConfigReader.getVesselOnlineThresholdMinutes();

        System.out.println("Configured online threshold: "
                + thresholdMinutes + " minutes");

        System.out.println("Dashboard Last Updated vs Current UTC: "
                + dashboardDiffFromNow + " minutes");

        System.out.println("Tooltip Time vs Current UTC: "
                + tooltipDiffFromNow + " minutes");

        // ---------- Vessel Connectivity Decision ----------
        boolean isDashboardFresh = dashboardDiffFromNow <= thresholdMinutes;
        boolean isTooltipFresh = tooltipDiffFromNow <= thresholdMinutes;

        if (isDashboardFresh && isTooltipFresh) {
            System.out.println("Vessel Status: ONLINE");
        } else {
            System.out.println("Vessel Status: OFFLINE");
        }

        // ---------- Final Assertion ----------
        Assert.assertTrue(
                isDashboardFresh && isTooltipFresh,
                "Vessel is OFFLINE: dashboard or tooltip data older than "
                        + thresholdMinutes + " minutes");
    }
}
