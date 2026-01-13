package com.smartshiphub.pages.LoginPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.smartshiphub.utils.ElementActions;
import com.smartshiphub.utils.WaitUtils;

public class LoginPage {

    private WebDriver driver;
    private WaitUtils wait;
    private ElementActions actions;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
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

    private By loginErrorMessageLocator =
            By.xpath("//div[contains(@class,'error') or contains(text(),'username')]");

    private By hamburgerMenuLocator =
            By.xpath("//i[contains(@class,'fa-bars')]");

    public void enterEmail(String text) {
        actions.type(email, text);
    }

    public void enterPassword(String text) {
        actions.type(password, text);
    }

    public void clickLogin() {
        actions.click(loginBtn);
    }

    public boolean isHamburgerMenuDisplayed() {
        WebElement ele = wait.waitForVisible(hamburgerMenuLocator);
        return ele.isDisplayed();
    }

    public boolean isLoginErrorDisplayed() {
        try {
            return wait.waitForVisible(loginErrorMessageLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoginErrorMessage() {
        WebElement error =
                wait.waitForVisible(loginErrorMessageLocator);
        return error.getText().trim();
    }
}
