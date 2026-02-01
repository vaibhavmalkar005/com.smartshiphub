package com.smartshiphub.pages.LoginPage;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.smartshiphub.utils.ElementActions;
import com.smartshiphub.utils.WaitUtils;

public class LoginPage {

    private ElementActions actions;
    private WaitUtils wait;

    public LoginPage(WebDriver driver) {
        this.actions = new ElementActions(driver);
        this.wait = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@placeholder='eg. john@abc.com']")
    private WebElement email;

    @FindBy(xpath = "//input[@placeholder='password']")
    private WebElement password;

    @FindBy(xpath = "//button[contains(text(),'Login')]")
    private WebElement loginBtn;

    private By dashboardIndicator =
            By.xpath("//div[contains(@class,'dashboard')]");

    private By errorMsg =
            By.cssSelector("div.cg-notify-message.alert-danger");

    public boolean isLoginPageVisible() {
        try {
            return email.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** ✅ USE THIS EVERYWHERE */
    public void loginIfRequired(String user, String pass) {

        if (isLoginPageVisible()) {
            actions.type(email, user);
            actions.type(password, pass);
            actions.click(loginBtn);
        }

        wait.waitForVisible(dashboardIndicator);
    }

    /** Used ONLY by LoginTest */
    public void login(String user, String pass) {
        wait.waitForVisible(email);
        actions.type(email, user);
        actions.type(password, pass);
        actions.click(loginBtn);
    }

    public boolean isLoginSuccessful() {
        return wait.waitForVisible(dashboardIndicator).isDisplayed();
    }

    public boolean isErrorDisplayed() {
        try {
            return wait.waitForVisible(errorMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return wait.waitForVisible(errorMsg)
                .getText().replace("×", "").trim();
    }
}
