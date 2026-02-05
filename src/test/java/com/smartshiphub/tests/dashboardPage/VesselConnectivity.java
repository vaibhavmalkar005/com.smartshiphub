package com.smartshiphub.tests.dashboardPage;

import java.time.*;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.smartshiphub.vesselreport.VesselReportStore;
import com.smartshiphub.vesselreport.VesselStatus;
import com.smartshiphub.base.BaseTest;
import com.smartshiphub.dataprovider.EnvironmentDataProvider;
import com.smartshiphub.listeners.TestListener;
import com.smartshiphub.pages.DashboardPage.DashboardPage;
import com.smartshiphub.pages.LoginPage.LoginPage;
import com.smartshiphub.reports.VesselReportUtil;
import com.smartshiphub.utils.ConfigReader;
import com.smartshiphub.utils.LoginHelper;

@Listeners(TestListener.class)
public class VesselConnectivity extends BaseTest {

	@Test(dataProvider = "instanceProvider", dataProviderClass = EnvironmentDataProvider.class, groups = { "sanity",
			"smoke" })
	public void verifyVesselConnectivityForAllVessels(String instance) throws Exception {

		launchApplication(instance);

		LoginPage login = new LoginPage(driver);
		String[] creds = LoginHelper.getValidLoginFromExcel();
		login.loginIfRequired(creds[0], creds[1]);

		/* ✅ Vessel Connectivity Report – Instance Header */
		VesselReportUtil.createInstanceHeader(instance);

		DashboardPage dashboard = new DashboardPage(driver);
		int thresholdMinutes = ConfigReader.getVesselOnlineThresholdMinutes();
		int vesselCount = dashboard.getVesselCount();

		Assert.assertTrue(vesselCount > 0, "No vessels found");

		for (int i = 0; i < vesselCount; i++) {

			String vesselName = dashboard.selectVesselByIndex(i);

			VesselReportUtil.createVesselHeader(vesselName);

			Reporter.log("\n========================================", true);
			Reporter.log("Checking Vessel: " + vesselName, true);

			LocalDateTime nowUTC = LocalDateTime.now(ZoneOffset.UTC);
			Reporter.log("Current UTC Time : " + nowUTC, true);

			/* ================= PRIORITY 1 ================= */

			String lastUpdatedText = dashboard.waitForLastUpdatedOrNA(20);
			Reporter.log("Last Updated Raw Text : " + lastUpdatedText, true);

			if ("NA".equalsIgnoreCase(lastUpdatedText)) {
				Reporter.log("DECISION → OFFLINE (LastUpdated is NA)", true);
				VesselReportUtil.logOffline("OFFLINE → LastUpdated is NA");
				VesselReportStore.add(new VesselStatus(instance, vesselName, false, "LastUpdated is NA"));
				continue;
			}

			LocalDateTime lastUpdatedUTC = parseDateTimeSafely(lastUpdatedText);
			if (lastUpdatedUTC == null) {
				Reporter.log("DECISION → OFFLINE (Invalid LastUpdated)", true);
				VesselReportUtil.logOffline("OFFLINE → Invalid LastUpdated format");
				continue;
			}

			long diffLastUpdated = Duration.between(lastUpdatedUTC, nowUTC).toMinutes();

			/* ================= PRIORITY 2 ================= */

			if (diffLastUpdated > thresholdMinutes) {
				Reporter.log("DECISION → OFFLINE (Threshold exceeded)", true);
				VesselReportUtil.logOffline("OFFLINE → LastUpdated exceeded threshold (" + thresholdMinutes + " mins)");
				VesselReportStore.add(new VesselStatus(instance, vesselName, false, "LastUpdated exceeded threshold"));
				continue;
			}

			/* ================= TOOLTIP ================= */

			String tooltipRaw = dashboard.getTooltipTimeFromGraph();
			Reporter.log("Graph Tooltip Time : " + tooltipRaw, true);

			if (tooltipRaw == null || tooltipRaw.trim().isEmpty()) {
				Reporter.log("DECISION → OFFLINE (Tooltip missing)", true);
				VesselReportUtil.logOffline("OFFLINE → Tooltip missing");
				VesselReportStore.add(new VesselStatus(instance, vesselName, false, "Tooltip missing"));
				continue;
			}

			LocalDateTime tooltipUTC = parseTooltipTimeSafely(tooltipRaw);
			if (tooltipUTC == null) {
				Reporter.log("DECISION → OFFLINE (Invalid tooltip)", true);
				VesselReportUtil.logOffline("OFFLINE → Invalid tooltip format");
				continue;
			}

			long diffTooltip = Duration.between(tooltipUTC, nowUTC).toMinutes();

			/* ================= FINAL DECISION ================= */

			if (diffLastUpdated > thresholdMinutes || diffTooltip > thresholdMinutes) {
				Reporter.log("DECISION → OFFLINE", true);
				VesselReportUtil.logOffline("OFFLINE → Time difference exceeded " + thresholdMinutes + " minutes");
			} else {
				Reporter.log("DECISION → ONLINE", true);
				VesselReportUtil.logOnline("ONLINE → Both timestamps within " + thresholdMinutes + " minutes");
				VesselReportStore.add(new VesselStatus(instance, vesselName, true, "ONLINE"));
			}
		}

		/* ✅ Flush ONLY Vessel Connectivity Report */
		// VesselReportUtil.flush();
	}

	/* ================= SAFE PARSERS ================= */

	private LocalDateTime parseDateTimeSafely(String raw) {
		if (raw == null)
			return null;
		raw = raw.replaceFirst("^:\\s*", "").trim();

		if (raw.isEmpty() || raw.equalsIgnoreCase("NA"))
			return null;

		try {
			return LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		} catch (Exception e) {
			try {
				return LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} catch (Exception ex) {
				return null;
			}
		}
	}

	private LocalDateTime parseTooltipTimeSafely(String raw) {
		if (raw == null || raw.trim().isEmpty())
			return null;

		try {
			return LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} catch (Exception ignore) {
		}

		try {
			String date = raw.substring(0, 10);
			String time = raw.substring(11);
			if (time.length() == 6) {
				String normalized = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
				return LocalDateTime.parse(date + " " + normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			}
		} catch (Exception ignore) {
		}

		return null;
	}

}
