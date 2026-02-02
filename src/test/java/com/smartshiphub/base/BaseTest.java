package com.smartshiphub.base;

import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.smartshiphub.factory.DriverFactory;
import com.smartshiphub.utils.ConfigReader;

public class BaseTest {

    protected WebDriver driver;
    protected Properties prop;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        prop = ConfigReader.initProperties();
        driver = DriverFactory.initDriver(prop.getProperty("browser"));
        driver.get(prop.getProperty("url"));
    }

    protected void launchApplication(String instance) {
        String environment = System.getProperty("environment", "PROD");
        String url = "https://www.smartshipweb.com/" + instance + "/#/UserLogin";
        System.out.println("Launching URL: " + url);
        driver.get(url);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
