package com.smartshiphub.pages.DashboardPage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.smartshiphub.utils.WaitUtils;

public class DashboardPage {

    private final WebDriver driver;
    private final WaitUtils wait;
    private final WebDriverWait explicitWait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        this.explicitWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ---------- Dashboard Last Updated ----------
    @FindBy(xpath = "//div[contains(text(),'Last Updated')]//b")
    private WebElement lastUpdatedDateTime;

    public String waitForLastUpdatedDateTime() {

        for (int i = 0; i < 15; i++) {
            String value = lastUpdatedDateTime.getText().trim();

            if (!value.equalsIgnoreCase("NA")
                    && !value.contains("NA")
                    && !value.isEmpty()) {
                return value;
            }
            wait.sleep(1000);
        }

        throw new RuntimeException("Last Updated time did not update from NA");
    }

    // ---------- Graph ----------
    private final By referenceArea =
            By.xpath("//*[name()='g' and contains(@class,'recharts-reference-area')]");

   public String getTooltipTimeFromGraph() {

    WebElement graph =
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(referenceArea));

    JavascriptExecutor js = (JavascriptExecutor) driver;

    // Activate graph
    js.executeScript(
            "arguments[0].dispatchEvent(new MouseEvent('click',{bubbles:true}))",
            graph);

    wait.sleep(2000);

    Point location = graph.getLocation();
    Dimension size = graph.getSize();

    int hoverX = location.getX() + size.getWidth() - 2;
    int hoverY = location.getY() + size.getHeight() / 2;

    for (int i = 0; i < 6; i++) {

        js.executeScript(
                "var evt=new MouseEvent('mousemove',{clientX:arguments[0],clientY:arguments[1],bubbles:true});"
                        + "var el=document.elementFromPoint(arguments[0],arguments[1]);"
                        + "if(el)el.dispatchEvent(evt);",
                hoverX, hoverY);

        wait.sleep(1500);

        List<WebElement> tooltipTime =
                driver.findElements(By.xpath(
                        "//div[contains(@class,'recharts-tooltip-wrapper')]//div[contains(text(),'Time')]"));

        if (!tooltipTime.isEmpty()) {
            return tooltipTime.get(0)
                    .getText()
                    .replace("Time :", "")
                    .trim();
        }
    }

    // ❗ Tooltip did not appear → treat as OFFLINE signal
    System.out.println("Tooltip did not open or time not available");
    return null;
}


    public LocalDateTime parseTooltipTime(String tooltipTime) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(tooltipTime, formatter);
    }
}
