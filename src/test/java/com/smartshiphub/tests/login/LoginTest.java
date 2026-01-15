package com.smartshiphub.tests.login;

import org.testng.Assert;
import org.testng.annotations.*;

import com.smartshiphub.base.BaseTest;
import com.smartshiphub.listeners.TestListener;
import com.smartshiphub.pages.LoginPage.LoginPage;
import com.smartshiphub.utils.ConfigReader;
import com.smartshiphub.utils.ExcelUtils;

@Listeners(TestListener.class)
public class LoginTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws Exception {
        ConfigReader.initProperties();
        return ExcelUtils.getLoginData(
                ConfigReader.get("excelPath"),
                ConfigReader.get("ExcelSheetName"));
    }

    @Test(dataProvider = "loginData")
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
            Assert.assertEquals(
                    login.getErrorMessage(),
                    expectedErrorMessage);
        }
    }
}
