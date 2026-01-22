package com.smartshiphub.pages.DashboardPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.smartshiphub.utils.ElementActions;
import com.smartshiphub.utils.WaitUtils;

public class DashboardPage {

    private WaitUtils wait;
    private ElementActions actions;

    public DashboardPage(WebDriver driver) {
        this.wait = new WaitUtils(driver);
        this.actions = new ElementActions(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(text(), 'Last Updated')]//b")
    private WebElement lastUpdatedDateTime;

    public String getLastUpdatedDateTime() {
        return lastUpdatedDateTime.getText().trim();
    }

    // ✅ ADD THIS METHOD
    public String waitForLastUpdatedDateTime() {

        for (int i = 0; i < 10; i++) {

            String text = lastUpdatedDateTime.getText().trim();

            if (!text.equalsIgnoreCase("NA")
                    && !text.contains("NA")
                    && !text.isEmpty()) {
                return text;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new RuntimeException(
                "Last Updated time did not update from NA within expected time");
    }
}
