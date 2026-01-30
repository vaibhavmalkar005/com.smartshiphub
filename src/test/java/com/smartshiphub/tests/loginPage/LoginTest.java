package com.smartshiphub.tests.loginPage;

import org.testng.Assert;
import org.testng.annotations.*;

import com.smartshiphub.base.BaseTest;
import com.smartshiphub.listeners.TestListener;
import com.smartshiphub.pages.LoginPage.LoginPage;
import com.smartshiphub.utils.ConfigReader;
import com.smartshiphub.utils.ExcelUtils;

@Listeners(TestListener.class)
public class LoginTest extends BaseTest {

    @DataProvider
    public Object[][] loginData() {
        ConfigReader.initProperties();
        return ExcelUtils.getLoginData(
            ConfigReader.get("excelPath"),
            ConfigReader.get("ExcelSheetName"));
    }

    @Test(dataProvider = "loginData", groups = {"sanity", "smoke", "regression"})
    public void verifyLoginScenarios(
            String testCase,
            String username,
            String password,
            String expectedResult,
            String expectedErrorMessage) {

        LoginPage login = new LoginPage(driver);
        login.login(username, password);

        if ("SUCCESS".equalsIgnoreCase(expectedResult)) {
            Assert.assertTrue(login.isLoginSuccessful());
        } else {
            Assert.assertTrue(login.isErrorDisplayed());
            Assert.assertEquals(login.getErrorMessage(), expectedErrorMessage);
        }
    }
}
