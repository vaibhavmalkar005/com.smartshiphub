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

    /* ================= HELPERS ================= */

    public boolean isLoginPageVisible() {
        try {
            return email.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /* ================= MAIN LOGIN ================= */

    /**
     * ✅ Smart login:
     * - Login if page visible
     * - Wait ONLY for (dashboard OR error)
     * - Fail FAST if login fails
     */
    public void loginIfRequired(String user, String pass) {

        if (isLoginPageVisible()) {
            actions.type(email, user);
            actions.type(password, pass);
            actions.click(loginBtn);
        }

        try {
            // ⏱ Short intelligent wait (NOT 20 sec dashboard wait)
            WebDriverWait shortWait =
                    new WebDriverWait(driver, Duration.ofSeconds(5));

            shortWait.until(d ->
                    d.findElements(dashboardIndicator).size() > 0
                 || d.findElements(errorMsg).size() > 0
            );

        } catch (TimeoutException e) {
            throw new RuntimeException(
                "LOGIN FAILED → No dashboard or error message appeared");
        }

        // ❌ Login error message shown
        if (!driver.findElements(errorMsg).isEmpty()) {
            String msg = driver.findElement(errorMsg)
                    .getText().replace("×", "").trim();
            throw new RuntimeException("LOGIN FAILED → " + msg);
        }

        // ❌ Dashboard still not visible
        if (driver.findElements(dashboardIndicator).isEmpty()) {
            throw new RuntimeException(
                "LOGIN FAILED → Dashboard did not load after login click");
        }
    }

    /* ================= USED BY LoginTest ONLY ================= */

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
