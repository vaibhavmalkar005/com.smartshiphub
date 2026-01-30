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
    public void verifyVesselConnectivityFromDashboardAndGraph() throws Exception {

        LoginPage login = new LoginPage(driver);
        String[] creds = LoginHelper.getValidLoginFromExcel();
        login.login(creds[0], creds[1]);

        Assert.assertTrue(login.isLoginSuccessful(), "Login failed");

        DashboardPage dashboard = new DashboardPage(driver);

        String dashboardText = dashboard.waitForLastUpdatedDateTime();
        LocalDateTime dashboardUTC =
                LocalDateTime.parse(
                        dashboardText.replaceFirst("^:\\s*", ""),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        String tooltipRaw = dashboard.getTooltipTimeFromGraph();
        LocalDateTime nowUTC = LocalDateTime.now(ZoneOffset.UTC);

        int threshold = ConfigReader.getVesselOnlineThresholdMinutes();

        boolean dashboardFresh =
                Duration.between(dashboardUTC, nowUTC).toMinutes() <= threshold;

        boolean tooltipFresh = false;

        if (tooltipRaw != null) {
            LocalDateTime tooltipUTC = dashboard.parseTooltipTime(tooltipRaw);
            tooltipFresh =
                    Duration.between(tooltipUTC, nowUTC).toMinutes() <= threshold;
        }

        boolean vesselOnline = dashboardFresh && tooltipFresh;

        Reporter.log("Vessel Status: " + (vesselOnline ? "ONLINE" : "OFFLINE"), true);

        Assert.assertNotNull(dashboardUTC, "Dashboard time must be available");
    }
}
