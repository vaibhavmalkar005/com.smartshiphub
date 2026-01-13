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

    @BeforeMethod
    public void setup() throws Exception {
        prop = ConfigReader.initProperties();
        driver = DriverFactory.initDriver(prop.getProperty("browser"));
        driver.get(prop.getProperty("url")); 
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
