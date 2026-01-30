package com.smartshiphub.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {

    private static Properties prop;

    public static synchronized Properties initProperties() {
        try {
            if (prop == null) {
                prop = new Properties();
                FileInputStream fis =
                        new FileInputStream("src/test/resources/config.properties");
                prop.load(fis);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
        return prop;
    }

    public static String get(String key) {
        if (prop == null) {
            initProperties(); // 🔥 SAFETY NET
        }
        return prop.getProperty(key);
    }

    public static int getVesselOnlineThresholdMinutes() {
        return Integer.parseInt(get("vessel.online.threshold.minutes"));
    }
}
