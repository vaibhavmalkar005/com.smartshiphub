package com.smartshiphub.pages.DashboardPage;

import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.stream.Collectors;

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

        // 🔹 Trigger hover
        js.executeScript(
                "var e=new MouseEvent('mousemove',{clientX:arguments[0],clientY:arguments[1],bubbles:true});"
                        + "document.elementFromPoint(arguments[0],arguments[1]).dispatchEvent(e);",
                loc.getX() + size.getWidth() - 2,
                loc.getY() + size.getHeight() / 2);

        // 🔹 WAIT until tooltip text is actually populated
        WebDriverWait tooltipWait =
                new WebDriverWait(driver, Duration.ofSeconds(3));

        WebElement tooltip = tooltipWait.until(driver -> {
            List<WebElement> list = driver.findElements(tooltipTime);
            if (list.isEmpty()) return null;

            String text = list.get(0).getText();
            return (text != null && text.contains(":")) ? list.get(0) : null;
        });

        return tooltip.getText()
                .replace("Time", "")
                .replace(":", "")
                .trim();

    } catch (Exception e) {
        return null; // ✅ business logic expects this
    }
}


    /* ================= VESSEL DROPDOWN ================= */

    private By vesselDropdown =
        By.xpath("//div[@id='dropdownIdTwo']");

    private By vesselOptions =
        By.xpath("//div[contains(@class,'dashboard-vessel-select__option')]");

    /** Open dropdown ONCE */
    private void openVesselDropdownIfNeeded() {
        List<WebElement> vessels = driver.findElements(vesselOptions);
        if (vessels.isEmpty()) {
            WebElement dropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(vesselDropdown));
            dropdown.click();
            sleep(800);
        }
    }

    /** ✅ READ-ONLY list (NO REPEATED CLICKS) */
    public List<String> getVesselNames() {
        openVesselDropdownIfNeeded();

        return wait.until(ExpectedConditions
                .visibilityOfAllElementsLocatedBy(vesselOptions))
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    /** ✅ FAST count */
    public int getVesselCount() {
        return getVesselNames().size();
    }

    /** ✅ Click only when selection needed */
    public String selectVesselByIndex(int index) {

        openVesselDropdownIfNeeded();

        List<WebElement> vessels =
            wait.until(ExpectedConditions
                .visibilityOfAllElementsLocatedBy(vesselOptions));

        if (index >= vessels.size()) {
            throw new RuntimeException("Vessel index out of range: " + index);
        }

        String name = vessels.get(index).getText().trim();
        vessels.get(index).click();

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
