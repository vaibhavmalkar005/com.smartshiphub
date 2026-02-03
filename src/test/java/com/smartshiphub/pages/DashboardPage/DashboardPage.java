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

    /* ================= ELEMENTS ================= */
    @FindBy(xpath = "//div[contains(text(),'Last Updated')]//b")
    private WebElement lastUpdatedDateTime;

    private By tooltipTime = By.xpath("//div[contains(@class,'recharts-tooltip-wrapper')]//div[contains(text(),'Time')]");
    private By graphArea = By.xpath("//*[name()='g' and contains(@class,'recharts-reference-area')]");
    private By vesselDropdown = By.xpath("//div[@id='dropdownIdTwo']");
    private By vesselOptions = By.xpath("//div[contains(@class,'dashboard-vessel-select__option')]");

    /* ================= DASHBOARD DATA ================= */
    
    /**
     * Polling mechanism to wait for actual timestamp data.
     */
    public String waitForLastUpdatedOrNA(int maxSeconds) {
        for (int i = 0; i < maxSeconds; i++) {
            try {
                String value = lastUpdatedDateTime.getText().trim();
                if (!value.isEmpty() && !value.equalsIgnoreCase("NA")) {
                    return value;
                }
            } catch (Exception ignored) {}
            sleep(1000);
        }
        return "NA";
    }

    /**
     * Triggers a hover on the right edge of the graph to get the latest data point.
     */
    public String getTooltipTimeFromGraph() {
        try {
            WebElement graph = wait.until(ExpectedConditions.presenceOfElementLocated(graphArea));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            Point loc = graph.getLocation();
            Dimension size = graph.getSize();

            // Calculate far right point of graph for latest data
            int targetX = loc.getX() + size.getWidth() - 5;
            int targetY = loc.getY() + (size.getHeight() / 2);

            js.executeScript(
                "var e = new MouseEvent('mousemove', {clientX: arguments[0], clientY: arguments[1], bubbles: true});" +
                "document.elementFromPoint(arguments[0], arguments[1]).dispatchEvent(e);", 
                targetX, targetY
            );

            // Wait for tooltip to update with valid data
            return new WebDriverWait(driver, Duration.ofSeconds(4))
                .until(d -> {
                    List<WebElement> tooltips = d.findElements(tooltipTime);
                    if (tooltips.isEmpty()) return null;
                    String text = tooltips.get(0).getText();
                    return (text != null && text.contains(":")) ? text.replace("Time", "").replace(":", "").trim() : null;
                });
        } catch (Exception e) {
            return null; // Test logic handles null as 'Offline'
        }
    }

    /* ================= VESSEL DROPDOWN ================= */

    private void openVesselDropdown() {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(vesselDropdown));
        dropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(vesselOptions));
    }

    public List<String> getVesselNames() {
        openVesselDropdown();
        List<String> names = driver.findElements(vesselOptions).stream()
                .map(e -> e.getText().trim())
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
        
        // Close dropdown by clicking header again to clean up state
        driver.findElement(vesselDropdown).click();
        return names;
    }

    public int getVesselCount() {
        return getVesselNames().size();
    }

    public String selectVesselByIndex(int index) {
        openVesselDropdown();
        List<WebElement> vessels = driver.findElements(vesselOptions);
        
        if (index >= vessels.size()) {
            throw new IndexOutOfBoundsException("Vessel index " + index + " exceeds list size " + vessels.size());
        }

        String name = vessels.get(index).getText().trim();
        vessels.get(index).click();
        
        // Wait for dashboard to update after selection
        wait.until(ExpectedConditions.invisibilityOfElementLocated(vesselOptions));
        sleep(1500); 
        return name;
    }

    /* ================= UTIL ================= */
    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
