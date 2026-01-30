package com.smartshiphub.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {

    static Properties prop;

    public static Properties initProperties() throws Exception {
        if (prop == null) {
            prop = new Properties();
            FileInputStream fis =
                    new FileInputStream("src/test/resources/config.properties");
            prop.load(fis);
        }
        return prop;
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }

    public static int getVesselOnlineThresholdMinutes() {
        String value = get("vessel.online.threshold.minutes");
        return Integer.parseInt(value);
    }
}
