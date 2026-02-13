package com.smartshiphub.vesselreport;

import java.util.*;
import java.util.stream.Collectors;

public class VesselReportStore {

    /* ================= MODIFIED: Thread Safe List ================= */
    private static final List<VesselStatus> DATA =
            Collections.synchronizedList(new ArrayList<>());

    public static void add(VesselStatus status) {
        DATA.add(status);
    }

    public static List<VesselStatus> getAll() {
        return new ArrayList<>(DATA); // defensive copy
    }

    public static Map<String, List<VesselStatus>> byInstance() {
        return DATA.stream()
                .collect(Collectors.groupingBy(v -> v.instance));
    }

    public static Map<String, Long> offlineReasonCount() {
        return DATA.stream()
                .filter(v -> !v.online)
                .collect(Collectors.groupingBy(v -> v.reason, Collectors.counting()));
    }
}
