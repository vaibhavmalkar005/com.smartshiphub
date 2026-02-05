package com.smartshiphub.vesselreport;

import java.util.*;
import java.util.stream.Collectors;

public class VesselReportStore {

    private static final List<VesselStatus> DATA = new ArrayList<>();

    public static synchronized void add(VesselStatus status) {
        DATA.add(status);
    }

    public static List<VesselStatus> getAll() {
        return DATA;
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
