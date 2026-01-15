package com.smartshiphub.pages.LoginPage;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.smartshiphub.utils.ElementActions;
import com.smartshiphub.utils.WaitUtils;

public class LoginPage {

    private WaitUtils wait;
    private ElementActions actions;

    public LoginPage(WebDriver driver) {
        this.wait = new WaitUtils(driver);
        this.actions = new ElementActions(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@placeholder='eg. john@abc.com']")
    private WebElement email;

    @FindBy(xpath = "//input[@placeholder='password']")
    private WebElement password;

    @FindBy(xpath = "//button[contains(text(),'Login')]")
    private WebElement loginBtn;

    private By errorMsg =
            By.xpath("//div[contains(@class,'error')]");

    private By hamburgerMenu =
            By.xpath("//i[contains(@class,'fa-bars')]");

    public void login(String user, String pass) {
        actions.type(email, user);
        actions.type(password, pass);
        actions.click(loginBtn);
    }

    public boolean isLoginSuccessful() {
        return wait.waitForUrlContains("/DashboardHome")
                && wait.waitForVisible(hamburgerMenu).isDisplayed();
    }

    public boolean isErrorDisplayed() {
        return wait.waitForVisible(errorMsg).isDisplayed();
    }

    public String getErrorMessage() {
        return wait.waitForVisible(errorMsg).getText().trim();
    }
}
