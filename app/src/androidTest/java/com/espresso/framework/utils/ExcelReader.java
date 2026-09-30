package com.espresso.framework.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.espresso.framework.exceptions.DataReadException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel Reader Utility using Apache POI
 * Reads Excel files from assets folder
 * Fixed: Resource leaks, proper exception handling, input validation
 */
public class ExcelReader {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Read Excel file and return data as List of Maps
     * First row is treated as header
     * Fixed: Uses try-with-resources, throws exception on failure
     */
    public static List<Map<String, String>> readExcel(String filePath, String sheetName) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (sheetName == null || sheetName.isEmpty()) {
            throw new IllegalArgumentException("Sheet name cannot be null or empty");
        }

        List<Map<String, String>> data = new ArrayList<>();
        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        try (InputStream inputStream = context.getAssets().open(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new DataReadException("Sheet '" + sheetName + "' not found in file: " + filePath);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new DataReadException("Header row not found in sheet: " + sheetName);
            }

            // Extract headers
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell));
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String cellValue = cell != null ? getCellValue(cell) : "";
                        rowData.put(headers.get(j), cellValue);
                    }
                    data.add(rowData);
                }
            }

            logger.info("Successfully read {} rows from sheet '{}'", data.size(), sheetName);
            return data;

        } catch (IOException e) {
            logger.error("Failed to read Excel file: {}", filePath, e);
            throw new DataReadException(filePath, "Excel", e);
        }
    }

    /**
     * Get test data in Object[][] format for TestNG/JUnit data providers
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        List<Map<String, String>> data = readExcel(filePath, sheetName);
        Object[][] testData = new Object[data.size()][1];

        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i);
        }

        return testData;
    }

    /**
     * Read specific cell value from Excel
     * Fixed: Uses try-with-resources, throws exception on failure
     */
    public static String getCellValue(String filePath, String sheetName, int rowNum, int colNum) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (sheetName == null || sheetName.isEmpty()) {
            throw new IllegalArgumentException("Sheet name cannot be null or empty");
        }
        if (rowNum < 0) {
            throw new IllegalArgumentException("Row number cannot be negative");
        }
        if (colNum < 0) {
            throw new IllegalArgumentException("Column number cannot be negative");
        }

        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        try (InputStream inputStream = context.getAssets().open(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new DataReadException("Sheet '" + sheetName + "' not found in file: " + filePath);
            }

            Row row = sheet.getRow(rowNum);
            if (row == null) {
                logger.warn("Row {} not found in sheet: {}, returning empty string", rowNum, sheetName);
                return "";
            }

            Cell cell = row.getCell(colNum);
            return cell != null ? getCellValue(cell) : "";

        } catch (IOException e) {
            logger.error("Failed to read cell value from Excel: {}", filePath, e);
            throw new DataReadException(filePath, "Excel", e);
        }
    }

    /**
     * Get cell value as string based on cell type
     * Fixed: Better date formatting, proper numeric handling
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Format date properly
                    return DATE_FORMAT.format(cell.getDateCellValue());
                } else {
                    // Check if it's a whole number or decimal
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    // Try to evaluate formula and return result
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    logger.warn("Could not evaluate formula, returning formula string");
                    return cell.getCellFormula();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Get row count from sheet
     * Fixed: Uses try-with-resources, throws exception on failure
     */
    public static int getRowCount(String filePath, String sheetName) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (sheetName == null || sheetName.isEmpty()) {
            throw new IllegalArgumentException("Sheet name cannot be null or empty");
        }

        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        try (InputStream inputStream = context.getAssets().open(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new DataReadException("Sheet '" + sheetName + "' not found in file: " + filePath);
            }

            int rowCount = sheet.getLastRowNum() + 1;
            logger.debug("Row count for sheet '{}': {}", sheetName, rowCount);
            return rowCount;

        } catch (IOException e) {
            logger.error("Failed to get row count: {}", filePath, e);
            throw new DataReadException(filePath, "Excel", e);
        }
    }

    /**
     * Get column count from sheet
     * Fixed: Uses try-with-resources, throws exception on failure
     */
    public static int getColumnCount(String filePath, String sheetName) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (sheetName == null || sheetName.isEmpty()) {
            throw new IllegalArgumentException("Sheet name cannot be null or empty");
        }

        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        try (InputStream inputStream = context.getAssets().open(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new DataReadException("Sheet '" + sheetName + "' not found in file: " + filePath);
            }

            Row row = sheet.getRow(0);
            if (row == null) {
                logger.warn("No rows found in sheet: {}", sheetName);
                return 0;
            }

            int colCount = row.getLastCellNum();
            logger.debug("Column count for sheet '{}': {}", sheetName, colCount);
            return colCount;

        } catch (IOException e) {
            logger.error("Failed to get column count: {}", filePath, e);
            throw new DataReadException(filePath, "Excel", e);
        }
    }
}
