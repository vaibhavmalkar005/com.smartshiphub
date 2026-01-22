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
    public void verifyVesselConnectivityFromDashboard() throws Exception {

        System.out.println("Vessel Connectivity Test Running");

        // ------------------ Step 1: Login ------------------
        ConfigReader.initProperties();

        LoginPage login = new LoginPage(driver);
        String[] creds = LoginHelper.getValidLoginFromExcel();
        login.login(creds[0], creds[1]);


        Assert.assertTrue(
                login.isLoginSuccessful(),
                "Login failed, Dashboard not loaded");

        // ------------------ Step 2: Dashboard Validation ------------------
        DashboardPage dashboardPage = new DashboardPage(driver);

        String lastUpdatedText = dashboardPage.waitForLastUpdatedDateTime();

        Assert.assertNotNull(
                lastUpdatedText,
                "Last Updated time is not visible on Dashboard");

        Assert.assertFalse(
                lastUpdatedText.equalsIgnoreCase("NA")
                || lastUpdatedText.contains("NA"),
                "Last Updated time is still NA after waiting"
        );

        System.out.println("Last Updated Time (UTC): " + lastUpdatedText);

     // ------------------ Step 3: Time Comparison ------------------

     // Remove only the leading colon and space (not time colons)
     String cleanedDateTime = lastUpdatedText.replaceFirst("^:\\s*", "");

     System.out.println("Cleaned Last Updated Time: " + cleanedDateTime);

     // UI format is dd-MM-yyyy HH:mm:ss
     DateTimeFormatter formatter =
             DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

     // Parse UI UTC time
     LocalDateTime lastUpdatedUTC =
             LocalDateTime.parse(cleanedDateTime, formatter);

     LocalDateTime currentUTC =
             LocalDateTime.now(ZoneOffset.UTC);

     long diffInMinutes =
             Duration.between(lastUpdatedUTC, currentUTC).toMinutes();

        // ------------------ Step 4: Connectivity Status ------------------
        if (diffInMinutes > 60) {
            System.out.println("❌ Vessel is OFFLINE");
            Assert.assertTrue(
                    diffInMinutes > 60,
                    "Vessel marked OFFLINE (last update > 1 hour)");
        } else {
            System.out.println("✅ Vessel is ONLINE");
            Assert.assertTrue(
                    diffInMinutes <= 60,
                    "Vessel marked ONLINE (last update within 1 hour)");
        }
    }
}
