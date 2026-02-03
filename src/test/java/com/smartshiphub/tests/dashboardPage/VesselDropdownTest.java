package com.smartshiphub.tests.dashboardPage;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.smartshiphub.base.BaseTest;
import com.smartshiphub.listeners.TestListener;
import com.smartshiphub.pages.DashboardPage.DashboardPage;
import com.smartshiphub.pages.LoginPage.LoginPage;
import com.smartshiphub.utils.LoginHelper;

@Listeners(TestListener.class)
public class VesselDropdownTest extends BaseTest {

    @Test(groups = {"sanity", "regression"})
    public void verifyVesselDropdown() throws Exception {

        LoginPage login = new LoginPage(driver);
        String[] creds = LoginHelper.getValidLoginFromExcel();
        login.loginIfRequired(creds[0], creds[1]);

        DashboardPage dashboard = new DashboardPage(driver);

        int vesselCount = dashboard.getVesselCount();
        Assert.assertTrue(vesselCount > 0);

        for (int i = 0; i < vesselCount; i++) {
            String vessel = dashboard.selectVesselByIndex(i);
            Reporter.log("Vessel: " + vessel, true);
        }
    }
}
