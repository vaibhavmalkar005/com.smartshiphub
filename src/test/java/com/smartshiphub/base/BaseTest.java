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
    protected String environment;
    protected String instance;

    @BeforeMethod(alwaysRun = true)
    public void setup() {

        prop = ConfigReader.initProperties();
        driver = DriverFactory.initDriver(prop.getProperty("browser"));

        // 🔥 BACKWARD COMPATIBILITY (OLD WORKING BEHAVIOUR)
        if (System.getProperty("instance") == null) {
            driver.get(prop.getProperty("url"));
        }
    }

    protected void launchApplication(String instance) {

        this.instance = instance;
        this.environment = System.getProperty("environment", "PROD");

        if (instance == null || instance.trim().isEmpty()) {
            throw new RuntimeException("Instance is NULL from DataProvider");
        }

        String url = EnvironmentUtil.buildLoginUrl(environment, instance);
        System.out.println("Launching URL: " + url);
        driver.get(url);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
