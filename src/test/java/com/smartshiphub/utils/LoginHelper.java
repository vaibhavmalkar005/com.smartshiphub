package com.smartshiphub.utils;

import com.smartshiphub.pages.LoginPage.LoginPage;

public class LoginHelper {


    public static String[] getValidLoginFromExcel() throws Exception {

        Object[][] data = ExcelUtils.getLoginData(
                ConfigReader.get("excelPath"),
                ConfigReader.get("ExcelSheetName"));

        for (Object[] row : data) {
            String expectedResult = row[3].toString();

            if ("SUCCESS".equalsIgnoreCase(expectedResult)) {
                return new String[]{
                        row[1].toString(), // username
                        row[2].toString()  // password
                };
            }
        }

        throw new RuntimeException("No SUCCESS login found in Excel");
    }
}
