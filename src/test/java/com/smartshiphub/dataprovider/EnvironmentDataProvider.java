package com.smartshiphub.dataprovider;

import java.util.List;

import org.testng.annotations.DataProvider;

import com.smartshiphub.utils.ConfigReader;
import com.smartshiphub.utils.ExcelUtils;

public class EnvironmentDataProvider {

    @DataProvider(name = "instanceProvider")
    public static Object[][] instanceProvider() {

        String environment =
                System.getProperty("environment", "PROD");

        List<String> instances =
                ExcelUtils.getExecutableInstances(
                        ConfigReader.get("envExcelPath"),
                        "Instances",
                        environment);

        Object[][] data = new Object[instances.size()][1];

        for (int i = 0; i < instances.size(); i++) {
            data[i][0] = instances.get(i);
        }

        return data;
    }
}
