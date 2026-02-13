package com.smartshiphub.base;

import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.smartshiphub.factory.DriverFactory;
import com.smartshiphub.utils.ConfigReader;
import com.smartshiphub.utils.EnvironmentUtil;

public class BaseTest {

    protected WebDriver driver;
    protected Properties prop;

    @BeforeMethod(alwaysRun = true)
    public void setup() {

        prop = ConfigReader.initProperties();

        driver = DriverFactory.initDriver(
                prop.getProperty("browser"));

        /* ================= OPTIONAL DEFAULT URL ================= */
        // driver.get(prop.getProperty("url"));
    }

    protected void launchApplication(String instance) {

        String environment =
                System.getProperty("environment", "PROD");

        String url =
                EnvironmentUtil.buildLoginUrl(environment, instance);

        driver.get(url);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        /* ================= SAFETY ADDED ================= */
        DriverFactory.quitDriver();
    }
}
