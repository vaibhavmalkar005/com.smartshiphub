package com.smartshiphub.tests.login;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
		return ExcelUtils.getLoginData(ConfigReader.get("excelPath"), ConfigReader.get("ExcelSheetName"));
	}

	@Test(dataProvider = "loginData")
	public void verifyLoginScenarios(String testCase, String username, String password, String expectedResult,
			String expectedErrorMessage) {

		Assert.assertNotNull(username, "Username is null for test case: " + testCase);
		Assert.assertNotNull(password, "Password is null for test case: " + testCase);

		LoginPage login = new LoginPage(driver);

		login.enterEmail(username);
		login.enterPassword(password);
		login.clickLogin();

		if ("SUCCESS".equalsIgnoreCase(expectedResult)) {

			Assert.assertTrue(login.isHamburgerMenuDisplayed(), "Login FAILED for test case: " + testCase);

		} else {

			Assert.assertTrue(login.isLoginErrorDisplayed(), "Error message NOT displayed for test case: " + testCase);

			Assert.assertEquals(login.getLoginErrorMessage(), expectedErrorMessage,
					"Error message mismatch for test case: " + testCase);
		}
	}
}