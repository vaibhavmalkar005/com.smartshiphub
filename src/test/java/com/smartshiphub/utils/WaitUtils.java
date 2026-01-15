package com.smartshiphub.utils;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class WaitUtils {

    WebDriver driver;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement waitForVisible(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    public boolean waitForUrlContains (String partialUrl) {
    	try {
    		return new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.urlContains(partialUrl));
    	} catch (TimeoutException e) {
    		return false;
    	}
    }
}

