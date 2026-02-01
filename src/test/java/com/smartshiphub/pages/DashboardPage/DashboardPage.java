package com.smartshiphub.pages.DashboardPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {

    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    /* ================= DASHBOARD TIME ================= */

    @FindBy(xpath = "//div[contains(text(),'Last Updated')]//b")
    private WebElement lastUpdatedDateTime;

    private By tooltipTime =
            By.xpath("//div[contains(@class,'recharts-tooltip-wrapper')]//div[contains(text(),'Time')]");

    private By graphArea =
            By.xpath("//*[name()='g' and contains(@class,'recharts-reference-area')]");

    public String waitForLastUpdatedOrNA(int maxSeconds) {

        for (int i = 0; i < maxSeconds; i++) {
            String value = lastUpdatedDateTime.getText().trim();
            if (!value.equalsIgnoreCase("NA") && !value.isEmpty()) {
                return value;
            }
            sleep(1000);
        }
        return "NA";
    }

    public String getTooltipTimeFromGraph() {
        try {
            WebElement graph = wait.until(
                    ExpectedConditions.presenceOfElementLocated(graphArea));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            Point loc = graph.getLocation();
            Dimension size = graph.getSize();

            js.executeScript(
                    "var e=new MouseEvent('mousemove',{clientX:arguments[0],clientY:arguments[1],bubbles:true});"
                            + "document.elementFromPoint(arguments[0],arguments[1]).dispatchEvent(e);",
                    loc.getX() + size.getWidth() - 2,
                    loc.getY() + size.getHeight() / 2);

            return driver.findElement(tooltipTime)
                    .getText().replace("Time :", "").trim();

        } catch (Exception e) {
            return null;
        }
    }

    /* ================= VESSEL DROPDOWN ================= */

    private By vesselDropdown =
            By.xpath("//div[@id='dropdownIdTwo']");

    private By vesselOptions =
            By.xpath("//div[contains(@class,'dashboard-vessel-select__option')]");

    public void openVesselDropdown() {

        WebElement dropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(vesselDropdown));

        actions.moveToElement(dropdown).click().perform();
        sleep(800); // animation + render
    }

    public List<WebElement> getVesselOptionsSafely() {

        for (int attempt = 0; attempt < 3; attempt++) {
            List<WebElement> vessels = driver.findElements(vesselOptions);
            if (!vessels.isEmpty()) {
                return vessels;
            }
            openVesselDropdown();
            sleep(1000);
        }
        return List.of();
    }

    public int getVesselCount() {
        openVesselDropdown();
        return getVesselOptionsSafely().size();
    }

    public String selectVesselByIndex(int index) {

        openVesselDropdown();
        List<WebElement> vessels = getVesselOptionsSafely();

        if (index >= vessels.size()) {
            throw new RuntimeException("Vessel index out of range: " + index);
        }

        WebElement vessel = vessels.get(index);
        String name = vessel.getText().trim();
        vessel.click();

        sleep(1200); // dashboard refresh
        return name;
    }

    /* ================= UTIL ================= */

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
