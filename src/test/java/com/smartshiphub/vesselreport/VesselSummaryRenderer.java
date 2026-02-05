package com.smartshiphub.vesselreport;

import java.util.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class VesselSummaryRenderer {

	/* 🔒 GUARD: ensure summary renders ONLY ONCE */
	private static boolean summaryRendered = false;

	public static synchronized void render(ExtentReports extent) {

		if (summaryRendered) {
			return;
		}
		summaryRendered = true;

		ExtentTest summary = extent.createTest("📊 FINAL Vessel Connectivity Summary");

		List<VesselStatus> all = VesselReportStore.getAll();

		if (all.isEmpty()) {
			summary.warning("No vessel data collected");
			return;
		}

		long online = all.stream().filter(v -> v.online).count();
		long offline = all.size() - online;

		/* ================= OVERALL STATUS ================= */

		String overallStatus = offline == 0 ? "🟢 HEALTHY" : offline <= 5 ? "🟠 DEGRADED" : "🔴 CRITICAL";

		summary.info("Overall Status : " + overallStatus);
		summary.info("Total Vessels  : " + all.size());
		summary.pass("Online Vessels : " + online);
		summary.fail("Offline Vessels: " + offline);

		/* ================= INSTANCE HEALTH TABLE ================= */

		StringBuilder table = new StringBuilder();
		table.append("<table style='border-collapse:collapse;width:70%;" + "font-size:14px;color:#000;"
				+ "background:#ffffff;' border='1'>")

				.append("<tr style='background:#0d6efd;color:white;text-align:center'>")
				.append("<tr style='background:#f9f9f9;text-align:center'>").append("<th>Instance</th>")
				.append("<th>Total</th>").append("<th>Online</th>").append("<th>Offline</th>").append("</tr>");

		Map<String, List<VesselStatus>> byInstance = VesselReportStore.byInstance();

		for (Map.Entry<String, List<VesselStatus>> entry : byInstance.entrySet()) {

			String instance = entry.getKey();
			List<VesselStatus> vessels = entry.getValue();

			long instOnline = vessels.stream().filter(v -> v.online).count();
			long instOffline = vessels.size() - instOnline;

			table.append("<tr>").append("<td>").append(instance).append("</td>").append("<td>").append(vessels.size())
					.append("</td>").append("<td style='color:green'>").append(instOnline).append("</td>")
					.append("<td style='color:red'>").append(instOffline).append("</td>").append("</tr>");
		}

		table.append("</table>");

		summary.info(MarkupHelper.createLabel(table.toString(), null));

		/* ================= OFFLINE DETAILS (INSTANCE + VESSEL) ================= */

		ExtentTest offlineNode = summary.createNode("❌ Offline Vessel Details");

		all.stream().filter(v -> !v.online)
				.forEach(v -> offlineNode.fail(v.instance + " → " + v.vessel + " → " + v.reason));
	}
}
