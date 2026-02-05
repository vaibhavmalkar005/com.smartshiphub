package com.smartshiphub.vesselreport;

public class VesselStatus {

    public String instance;
    public String vessel;
    public boolean online;
    public String reason;

    public VesselStatus(String instance, String vessel, boolean online, String reason) {
        this.instance = instance;
        this.vessel = vessel;
        this.online = online;
        this.reason = reason;
    }
}
