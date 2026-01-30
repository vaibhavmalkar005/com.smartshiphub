package com.smartshiphub.pages.DashboardPage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import com.smartshiphub.utils.WaitUtils;

public class DashboardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(text(),'Last Updated')]//b")
    private WebElement lastUpdatedDateTime;

    private By tooltipTime =
            By.xpath("//div[contains(@class,'recharts-tooltip-wrapper')]//div[contains(text(),'Time')]");

    private By graphArea =
            By.xpath("//*[name()='g' and contains(@class,'recharts-reference-area')]");

    public String waitForLastUpdatedDateTime() {

        for (int i = 0; i < 15; i++) {
            String value = lastUpdatedDateTime.getText().trim();
            if (!value.equalsIgnoreCase("NA") && !value.isEmpty()) {
                return value;
            }
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        throw new RuntimeException("Last Updated time not refreshed");
    }

    public String getTooltipTimeFromGraph() {

        WebElement graph = wait.until(ExpectedConditions.presenceOfElementLocated(graphArea));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        Point loc = graph.getLocation();
        Dimension size = graph.getSize();

        int x = loc.getX() + size.getWidth() - 2;
        int y = loc.getY() + size.getHeight() / 2;

        js.executeScript(
                "var e=new MouseEvent('mousemove',{clientX:arguments[0],clientY:arguments[1],bubbles:true});"
                        + "document.elementFromPoint(arguments[0],arguments[1]).dispatchEvent(e);",
                x, y);

        try {
            WebElement tooltip =
                    wait.until(ExpectedConditions.visibilityOfElementLocated(tooltipTime));

            return tooltip.getText().replace("Time :", "").trim();

        } catch (TimeoutException e) {
            return null; // OFFLINE
        }
    }

    public LocalDateTime parseTooltipTime(String tooltipTime) {
        return LocalDateTime.parse(
                tooltipTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
