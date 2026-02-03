package com.smartshiphub.utils;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ElementActions {

    private WebDriver driver;
    private WebDriverWait wait;

    public ElementActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void click(WebElement ele) {
        wait.until(ExpectedConditions.elementToBeClickable(ele));
        ele.click();
    }

    /**
     * Clears an input field and types the provided text into it.
     */
    public void type(WebElement ele, String text) {
        wait.until(ExpectedConditions.visibilityOf(ele));
        ele.clear();
        ele.sendKeys(text);
    }

    
    public void hover(WebElement ele) {
        wait.until(ExpectedConditions.visibilityOf(ele));
        new Actions(driver).moveToElement(ele).perform();
    }

    /**
     * Selects an option from a dropdown by visible text.
     */
    public void selectByText(WebElement ele, String text) {
        wait.until(ExpectedConditions.visibilityOf(ele));
        new Select(ele).selectByVisibleText(text);
    }

    /**
     * Selects an option from a dropdown by its value attribute.
     */
    public void selectByValue(WebElement ele, String value) {
        waitForVisibility(ele);
        new Select(ele).selectByValue(value);
    }

    /**
     * Selects an option from a dropdown by index.
     */
    public void selectByIndex(WebElement ele, int index) {
        waitForVisibility(ele);
        new Select(ele).selectByIndex(index);
    }

    /**
     * Waits until the WebElement is clickable.
     */
    public void waitForClickability(WebElement ele) {
        wait.until(ExpectedConditions.elementToBeClickable(ele));
    }

    /**
     * Waits until the WebElement is visible.
     */
    public void waitForVisibility(WebElement ele) {
        wait.until(ExpectedConditions.visibilityOf(ele));
    }


    public boolean isElementVisible(WebElement ele) {
        try {
            wait.until(ExpectedConditions.visibilityOf(ele));
            return ele.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

   
    public boolean isElementClickable(WebElement ele) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(ele));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
