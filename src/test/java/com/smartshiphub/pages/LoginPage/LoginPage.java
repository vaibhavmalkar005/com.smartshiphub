package com.smartshiphub.pages.LoginPage;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.smartshiphub.utils.ElementActions;
import com.smartshiphub.utils.WaitUtils;

public class LoginPage {

    private WebDriver driver;
    private ElementActions actions;
    private WaitUtils wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new ElementActions(driver);
        this.wait = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    /* ================= ELEMENTS ================= */

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

    /* ================= COMMON HELPERS ================= */

    public boolean isLoginPageVisible() {
        try {
            return email.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /* ===================================================
       ✅ LOGIN (USED BY ALL DASHBOARD TESTS)
       =================================================== */

    public boolean loginIfRequired(String user, String pass) {

        if (isLoginPageVisible()) {
            actions.type(email, user);
            actions.type(password, pass);
            actions.click(loginBtn);
        }

        try {
            WebDriverWait shortWait =
                new WebDriverWait(driver, Duration.ofSeconds(6));

            shortWait.until(d ->
                d.findElements(dashboardIndicator).size() > 0 ||
                d.findElements(errorMsg).size() > 0
            );

        } catch (TimeoutException e) {
            return false;
        }

        if (!driver.findElements(errorMsg).isEmpty()) {
            return false;
        }

        return !driver.findElements(dashboardIndicator).isEmpty();
    }

      /** Used ONLY by LoginTest */
    public void login(String user, String pass) {
        wait.waitForVisible(email);
        actions.type(email, user);
        actions.type(password, pass);
        actions.click(loginBtn);
    }

    /** Used ONLY by LoginTest */
    public boolean isLoginSuccessful() {
        try {
            return wait.waitForVisible(dashboardIndicator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Used ONLY by LoginTest */
    public boolean isErrorDisplayed() {
        try {
            return wait.waitForVisible(errorMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Used ONLY by LoginTest */
    public String getErrorMessage() {
        try {
            return wait.waitForVisible(errorMsg)
                .getText()
                .replace("×", "")
                .trim();
        } catch (Exception e) {
            return "";
        }
    }
}
