package com.emos.vcm.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.ArrayList;

public class ExcelProcess {
    // 将单元格的值转为String类型
    public static String getCellValueToString(HSSFCell cell) {
        String value = null;
        if (cell != null) {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    value = cell.getStringCellValue().trim();
                    break;
                case NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue()).trim();
                    break;
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue()).trim();
                    break;
                case FORMULA:
                    value = String.valueOf(cell.getCellFormula()).trim();
                    break;
                case BLANK:
                    value = "";
            }
        }
        return value;
    }

    public static Boolean isBlankRow(HSSFRow row) {
        if (row == null) {
            return true;
        } else {
            boolean result = true;
            String value;

            for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); ++i) {
                HSSFCell cell = row.getCell(i);
                value = getCellValueToString(cell);
                if (!value.trim().equals("")) {
                    result = false;
                    break;
                }
            }
            return result;
        }
    }

    // 返回表中真实存在的行数
    public static int getRealNumberOfRow(HSSFSheet sheet) {
        int num = 0;
        if (sheet != null) {
            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); ++i) {
                if (!isBlankRow(sheet.getRow(i)).booleanValue()) {
                    ++num;
                }
            }
        }
        return num;
    }

    // 返回表中标题为title的列序号，不存在返回-1
    public static int getTitleIndexOfSheet(HSSFSheet sheet, String title) {
        int index = -1;
        HSSFRow titles = sheet.getRow(0);
        for (int i = titles.getFirstCellNum(); i < titles.getLastCellNum(); i++) {
            HSSFCell tmp = titles.getCell(i);
            if (tmp != null) {
                if (tmp.getStringCellValue().trim().equals(title.trim())) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    // 获取特定列的值
    public static ArrayList<String> getColumnOfSheet(HSSFSheet sheet, int columnIndex) {
        HSSFRow titles = sheet.getRow(sheet.getFirstRowNum());      // 表标题行
        int firstColumnNum = titles.getFirstCellNum();             // 第一列的列序号（从0开始）
        int lastColumnNum = titles.getLastCellNum() - 1;            // 最后一列的列序号
        ArrayList<String> column = new ArrayList<String>();

        if (columnIndex >= firstColumnNum && columnIndex <= lastColumnNum) {
            // 从第二行开始，不包括标题
            for (int i = sheet.getFirstRowNum() + 1; i < getRealNumberOfRow(sheet); i++) {
                HSSFCell cell = sheet.getRow(i).getCell(columnIndex);
                column.add(getCellValueToString(cell));
            }
            return column;
        } else {
            return null;
        }
    }

    // String数组转为double数组
    public static double[] stringArrayToDoubleArray(ArrayList<String> array) {
        if (array != null) {
            double[] newArray = new double[array.size()];
            for (int i = 0; i < array.size(); i++) {
                newArray[i] = Double.parseDouble(array.get(i));
            }
            return newArray;
        } else {
            return null;
        }
    }
}
