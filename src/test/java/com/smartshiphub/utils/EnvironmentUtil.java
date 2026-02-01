package com.smartshiphub.utils;

public class EnvironmentUtil {

    public static String getBaseDomain(String environment) {

        if ("PROD".equalsIgnoreCase(environment)) {
            return "https://www.smartshipweb.com";
        }
        if ("UAT".equalsIgnoreCase(environment)) {
            return "https://uat.smartshipweb.com";
        }
        throw new RuntimeException("Invalid Environment: " + environment);
    }

    public static String buildLoginUrl(String environment, String instance) {

        return getBaseDomain(environment)
                + "/" + instance
                + "/#/UserLogin";
    }
}
