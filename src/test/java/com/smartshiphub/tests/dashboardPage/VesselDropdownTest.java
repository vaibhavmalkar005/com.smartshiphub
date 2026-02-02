package com.smartshiphub.tests.dashboardPage;

import java.util.List;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.smartshiphub.base.BaseTest;
import com.smartshiphub.dataprovider.EnvironmentDataProvider;
import com.smartshiphub.listeners.TestListener;
import com.smartshiphub.pages.DashboardPage.DashboardPage;
import com.smartshiphub.pages.LoginPage.LoginPage;
import com.smartshiphub.utils.LoginHelper;

@Listeners(TestListener.class)
public class VesselDropdownTest extends BaseTest {

    @Test(
        dataProvider = "instanceProvider",
        dataProviderClass = EnvironmentDataProvider.class,
        groups = {"sanity", "regression"}
    )
    public void verifyVesselDropdown(String instance) throws Exception {

        launchApplication(instance);

        LoginPage login = new LoginPage(driver);
        String[] creds = LoginHelper.getValidLoginFromExcel();
        login.loginIfRequired(creds[0], creds[1]);

        DashboardPage dashboard = new DashboardPage(driver);

        /* ✅ Read-only operation (NO repeated clicks) */
        List<String> vessels = dashboard.getVesselNames();

        Assert.assertTrue(vessels.size() > 0, "No vessels found in dropdown");

        Reporter.log("Total Vessels: " + vessels.size(), true);

        for (String vessel : vessels) {
            Reporter.log("Vessel: " + vessel, true);
        }
    }
}
