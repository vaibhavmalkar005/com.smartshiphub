package com.smartshiphub.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver initDriver(String browserFromConfig) {

        /* ================= MODIFIED ================= */
        String browser =
                System.getProperty("browser",
                        browserFromConfig);

        boolean headless =
                Boolean.parseBoolean(
                        System.getProperty("headless", "false"));

        if ("chrome".equalsIgnoreCase(browser)) {

            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");

            if (headless) {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080");
            }

            driver.set(new ChromeDriver(options));

        } else if ("edge".equalsIgnoreCase(browser)) {

            WebDriverManager.edgedriver().setup();
            driver.set(new EdgeDriver());

        } else {
            throw new RuntimeException("Invalid browser: " + browser);
        }

        return getDriver();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {

        /* ================= SAFETY ADDED ================= */
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
