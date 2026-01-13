package com.smartshiphub.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtils {

    public static Object[][] getLoginData(String excelPath, String sheetName) {

        List<Object[]> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            int lastRowNum = sheet.getLastRowNum(); 

            for (int i = 1; i <= lastRowNum; i++) { 
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    continue; 
                }

                Object[] rowData = new Object[5];

                for (int j = 0; j < 5; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData[j] = cell.toString().trim();
                }

                dataList.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel data", e);
        }

        return dataList.toArray(new Object[0][]);
    }

    private static boolean isRowEmpty(Row row) {
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
