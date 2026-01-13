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

    // -------------------------------------------------------------------------
    // BASIC ACTIONS
    // -------------------------------------------------------------------------

    /**
     * Clicks a WebElement after waiting for it to be clickable.
     */
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

    /**
     * Performs a mouse hover action on the given WebElement.
     */
    public void hover(WebElement ele) {
        wait.until(ExpectedConditions.visibilityOf(ele));
        new Actions(driver).moveToElement(ele).perform();
    }

    // -------------------------------------------------------------------------
    // SELECT DROPDOWN METHODS
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // EXPLICIT WAIT UTILITIES
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // VALIDATION METHODS
    // -------------------------------------------------------------------------

    /**
     * Checks if the element is visible on the page.
     * Returns false if an exception occurs.
     */
    public boolean isElementVisible(WebElement ele) {
        try {
            wait.until(ExpectedConditions.visibilityOf(ele));
            return ele.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the element is in a clickable state.
     * Returns false if wait fails.
     */
    public boolean isElementClickable(WebElement ele) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(ele));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
