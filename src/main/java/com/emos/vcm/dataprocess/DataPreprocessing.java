package com.emos.vcm.dataprocess;

import com.emos.vcm.util.CopySheets;
import com.emos.vcm.util.ExcelProcess;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.ejml.simple.SimpleMatrix;

import java.io.*;
import java.util.*;

public class DataPreprocessing {
    public DataPreprocessing() {
    }

    // 返回两个点之间的直线距离
    public static double getDistanceOfPoint(double lat1, double lng1, double lat2, double lng2) {
        double Radius = 6370996.81;     //球半径
        double distance;

        if (lat1 == lat2 && lng1 == lng2) {
            distance = 0;
        } else {
            double a_lat = lat1 * Math.PI / 180;
            double a_lng = lng1 * Math.PI / 180;
            double b_lat = lat2 * Math.PI / 180;
            double b_lng = lng2 * Math.PI / 180;

            distance = Radius * Math.acos(Math.sin(a_lat) * Math.sin(b_lat) + Math.cos(a_lat) * Math.cos(b_lat) * Math.cos(b_lng - a_lng));
            distance /= 1000.0;
        }
        return distance;
    }

    public static double[][] computeDistanceMatrix(double[] lat1, double[] lng1, double[] lat2, double[] lng2) {
        if (lat1.length == lng1.length && lat2.length == lng2.length) {
            int length1 = lat1.length;
            int length2 = lat2.length;
            double[][] similarMatrix = new double[length1][length2];
            for (int i = 0; i < length1; i++) {
                for (int j = 0; j < length2; j++) {
                    similarMatrix[i][j] = getDistanceOfPoint(lat1[i], lng1[i], lat2[j], lng2[j]);
                }
            }
            return similarMatrix;
        } else {
            return null;
        }
    }

    // 计算sqrt(A.^2 + B.^2)
    public static double[][] averageSquareDistance(double[][] A, double[][] B) {
        if (A.length == B.length && A[0].length == B[0].length) {   // 数组维度相同
            double[][] C = new double[A.length][A[0].length];
            for (int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[0].length; j++) {
                    C[i][j] = Math.sqrt(Math.pow(A[i][j], 2) + Math.pow(B[i][j], 2));
//                    C[i][j] = (A[i][j] + B[i][j]) / 2.0;
                }
            }
            return C;
        } else {
            return null;
        }
    }

    // 根据车长和车板类型两个属性，为车辆添加该车型所具有的其他属性（车宽，车高，最大载重，最大体积）
    public static void addVehicleFeature(HSSFRow vehicleRow, int vehicleLengthIndex, int vehicleTypeIndex, HSSFSheet vehicleTypeSheet) {
        String vehicleLength = ExcelProcess.getCellValueToString(vehicleRow.getCell(vehicleLengthIndex));
        String vehicleType = ExcelProcess.getCellValueToString(vehicleRow.getCell(vehicleTypeIndex));

        int lengthIndex = ExcelProcess.getTitleIndexOfSheet(vehicleTypeSheet, "车长");
        int typeIndex = ExcelProcess.getTitleIndexOfSheet(vehicleTypeSheet, "车板");
        int widthIndex = ExcelProcess.getTitleIndexOfSheet(vehicleTypeSheet, "车宽");
        int heigthIndex = ExcelProcess.getTitleIndexOfSheet(vehicleTypeSheet, "车高");
        int weightIndex = ExcelProcess.getTitleIndexOfSheet(vehicleTypeSheet, "最大载重");
        int volumeIndex = ExcelProcess.getTitleIndexOfSheet(vehicleTypeSheet, "最大体积");

        int numOfCells = vehicleRow.getLastCellNum();

        for (int i = 0; i < ExcelProcess.getRealNumberOfRow(vehicleTypeSheet); i++) {
            HSSFRow rowTmp = vehicleTypeSheet.getRow(i);
            // 找到属于该车的车型
            if (vehicleLength.equals(ExcelProcess.getCellValueToString(rowTmp.getCell(lengthIndex))) &&
                    vehicleType.equals(ExcelProcess.getCellValueToString(rowTmp.getCell(typeIndex)))) {
                HSSFCell cell1 = vehicleRow.createCell(vehicleRow.getLastCellNum());
                cell1.setCellValue(rowTmp.getCell(widthIndex).getNumericCellValue());
                HSSFCell cell2 = vehicleRow.createCell(vehicleRow.getLastCellNum());
                cell2.setCellValue(rowTmp.getCell(heigthIndex).getNumericCellValue());
                HSSFCell cell3 = vehicleRow.createCell(vehicleRow.getLastCellNum());
                cell3.setCellValue(rowTmp.getCell(weightIndex).getNumericCellValue());
                HSSFCell cell4 = vehicleRow.createCell(vehicleRow.getLastCellNum());
                cell4.setCellValue(rowTmp.getCell(volumeIndex).getNumericCellValue());
            }
        }

        // 若无法查找到该车型，就将该车从数据中删除
        if (vehicleRow.getLastCellNum() == numOfCells) {
            vehicleRow.getSheet().removeRow(vehicleRow);
        }
    }

    // 将订单信息按照相似度分组
    public static void groupOrders(String ordersFile, String ordersGroupsFile) throws IOException {
        InputStream inp = new FileInputStream(new File(ordersFile));
        HSSFWorkbook wb = new HSSFWorkbook(inp);
        HSSFSheet sheet = wb.getSheetAt(0);
        int length = ExcelProcess.getRealNumberOfRow(sheet);     // 表的实际行数

        int pickupLatIndex = ExcelProcess.getTitleIndexOfSheet(sheet, "发货地址维度");
        int pickupLngIndex = ExcelProcess.getTitleIndexOfSheet(sheet, "发货地址经度");
        int deliveryLatIndex = ExcelProcess.getTitleIndexOfSheet(sheet, "收货地址维度");
        int deliveryLngIndex = ExcelProcess.getTitleIndexOfSheet(sheet, "收货地址经度");

        ArrayList<String> pickupLatTmp = ExcelProcess.getColumnOfSheet(sheet, pickupLatIndex);
        ArrayList<String> pickupLngTmp = ExcelProcess.getColumnOfSheet(sheet, pickupLngIndex);
        ArrayList<String> deliveryLatTmp = ExcelProcess.getColumnOfSheet(sheet, deliveryLatIndex);
        ArrayList<String> deliveryLngTmp = ExcelProcess.getColumnOfSheet(sheet, deliveryLngIndex);

        double[] pickupLat = ExcelProcess.stringArrayToDoubleArray(pickupLatTmp);
        double[] pickupLng = ExcelProcess.stringArrayToDoubleArray(pickupLngTmp);
        double[] deliveryLat = ExcelProcess.stringArrayToDoubleArray(deliveryLatTmp);
        double[] deliveryLng = ExcelProcess.stringArrayToDoubleArray(deliveryLngTmp);

        double[][] ordersPickupDistanceMatrix = computeDistanceMatrix(pickupLat, pickupLng, pickupLat, pickupLng);
        double[][] ordersDeliveryDistanceMatrix = computeDistanceMatrix(deliveryLat, deliveryLng, deliveryLat, deliveryLng);
        double[][] ordersSimilarMatrix = averageSquareDistance(ordersPickupDistanceMatrix, ordersDeliveryDistanceMatrix);

//            // 将相似度矩阵写入文件
//            HSSFWorkbook matrixWb = new HSSFWorkbook();
//            HSSFSheet matrixSheet = matrixWb.createSheet("SimilarMatrix");


        ArrayList<Set<Integer>> classes = new ArrayList<Set<Integer>>();
        for (int i = 0; i < ordersSimilarMatrix.length; i++) {
            for (int j = i + 1; j < ordersSimilarMatrix[0].length; j++) {
                if (ordersSimilarMatrix[i][j] <= 40) {
                    Set<Integer> set = new HashSet<Integer>();
                    set.add(i);
                    set.add(j);
                    classes.add(set);
                }
            }
        }

        for (int i = 0; i < classes.size() - 1; i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                //计算i与j集合之间的交集
                Set<Integer> result = new HashSet<Integer>();
                result.addAll(classes.get(i));
                result.retainAll(classes.get(j));

                // 若有交集，则合并，并移除j集合
                if (!result.isEmpty()) {
                    classes.get(i).addAll(classes.get(j));
                    classes.remove(j);
                    j--;    // 由于删除一个集合，索引应减1
                }
            }
        }
        for (Set<Integer> set : classes) {
            System.out.println(set);
        }

        // 将类别写入新的Excel表中
        HSSFWorkbook newWb = new HSSFWorkbook();
        HSSFRow titles = sheet.getRow(0);   // 取原表格的标题
        for (int i = 0; i < classes.size(); i++) {
            HSSFSheet sheetTmp = newWb.createSheet(String.valueOf(i));
            // 存标题
            HSSFRow newTitles = sheetTmp.createRow(0);
            Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
            CopySheets.copyRow(sheet, sheetTmp, titles, newTitles, styleMap);

            // 将分组后的订单存入sheet中
            Set<Integer> setTmp = classes.get(i);   // 第i组集合
            int newIndex = 1;
            for (Integer oldIndex : setTmp) {
                HSSFRow newRow = sheetTmp.createRow(newIndex);
                CopySheets.copyRow(sheet, sheetTmp, sheet.getRow(oldIndex + 1), newRow, styleMap);
                newIndex++;
            }

            // 自动调整sheet的列宽
            for (int j = 0; j < sheetTmp.getRow(sheetTmp.getFirstRowNum()).getLastCellNum(); j++) {
                sheetTmp.autoSizeColumn(j);
            }
        }
        // 将创建的工作表存入文件中
        OutputStream os = new FileOutputStream(new File(ordersGroupsFile));
        newWb.write(os);

        newWb.close();
        os.close();
        inp.close();
        wb.close();
    }

    // 根据订单组sheet的信息得到待选的车辆信息
    public static void groupVehicle(String vehicleFile, String vehicleTypeFile, HSSFSheet ordersGroupSheet, HSSFSheet vehicleGroupSheet) throws IOException {
        InputStream insVF = new FileInputStream(new File(vehicleFile));
        HSSFWorkbook wbVF = new HSSFWorkbook(insVF);
        HSSFSheet vehicleSheet = wbVF.getSheetAt(0);     // 车辆信息工作表

        int vehiclePositionLatIndex = ExcelProcess.getTitleIndexOfSheet(vehicleSheet, "车辆位置维度");
        int vehiclePositionLngIndex = ExcelProcess.getTitleIndexOfSheet(vehicleSheet, "车辆位置经度");
        ArrayList<String> vehiclePositionLatTmp = ExcelProcess.getColumnOfSheet(vehicleSheet, vehiclePositionLatIndex);
        ArrayList<String> vehiclePositionLngTmp = ExcelProcess.getColumnOfSheet(vehicleSheet, vehiclePositionLngIndex);
        double[] vehiclePositionLat = ExcelProcess.stringArrayToDoubleArray(vehiclePositionLatTmp);  // 车辆信息维度数组
        double[] vehiclePositionLng = ExcelProcess.stringArrayToDoubleArray(vehiclePositionLngTmp);  // 车辆信息经度数组

        int pickupLatIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址维度");
        int pickupLngIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址经度");
        ArrayList<String> pickupLatTmp = ExcelProcess.getColumnOfSheet(ordersGroupSheet, pickupLatIndex);
        ArrayList<String> pickupLngTmp = ExcelProcess.getColumnOfSheet(ordersGroupSheet, pickupLngIndex);
        double[] pickupLat = ExcelProcess.stringArrayToDoubleArray(pickupLatTmp);    // 订单取货地址维度数组
        double[] pickupLng = ExcelProcess.stringArrayToDoubleArray(pickupLngTmp);    // 订单取货地址经度数组

        double[][] vehiclePickupDistanceMatrixTmp = computeDistanceMatrix(pickupLat, pickupLng, vehiclePositionLat, vehiclePositionLng);
        Set<Integer> vehicleGroup = new HashSet<Integer>();     // 存放待选车辆的集合

        // 计算距离矩阵中，每辆车距取货地址的平均价距离小于150km的为待选车辆
        SimpleMatrix vehiclePickupDistanceMatrix = new SimpleMatrix(vehiclePickupDistanceMatrixTmp);
        for (int columnIndex = 0; columnIndex < vehiclePickupDistanceMatrix.numCols(); columnIndex++) {
            SimpleMatrix columnVector = vehiclePickupDistanceMatrix.extractVector(false, columnIndex);
            if (columnVector.elementSum() / columnVector.numRows() <= 150) {
                vehicleGroup.add(columnIndex);
            }
        }

        HSSFRow newTitle = vehicleGroupSheet.createRow(0);
        Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
        CopySheets.copyRow(vehicleSheet, vehicleGroupSheet, vehicleSheet.getRow(0), newTitle, styleMap);
        HSSFCell cell1 = newTitle.createCell(newTitle.getLastCellNum());
        cell1.setCellValue("车宽");
        HSSFCell cell2 = newTitle.createCell(newTitle.getLastCellNum());
        cell2.setCellValue("车高");
        HSSFCell cell3 = newTitle.createCell(newTitle.getLastCellNum());
        cell3.setCellValue("最大载重");
        HSSFCell cell4 = newTitle.createCell(newTitle.getLastCellNum());
        cell4.setCellValue("最大体积");

        int vehicleLengthIndex = ExcelProcess.getTitleIndexOfSheet(vehicleSheet, "车长");
        int vehicleTypeIndex = ExcelProcess.getTitleIndexOfSheet(vehicleSheet, "车板类型");

        InputStream insVTF = new FileInputStream(new File(vehicleTypeFile));
        HSSFWorkbook wbVTF = new HSSFWorkbook(insVTF);
        HSSFSheet vehicleTypeSheet = wbVTF.getSheetAt(0);    // 车辆类型信息工作表

        for (Integer vehicleIndex : vehicleGroup) {
            int newIndex = ExcelProcess.getRealNumberOfRow(vehicleGroupSheet);
            HSSFRow newRow = vehicleGroupSheet.createRow(newIndex);
            CopySheets.copyRow(vehicleSheet, vehicleGroupSheet, vehicleSheet.getRow(vehicleIndex + 1), newRow, styleMap);
            addVehicleFeature(newRow, vehicleLengthIndex, vehicleTypeIndex, vehicleTypeSheet);
        }

        // 自动调整sheet的列宽
        for (int j = 0; j < vehicleGroupSheet.getRow(vehicleGroupSheet.getFirstRowNum()).getLastCellNum(); j++) {
            vehicleGroupSheet.autoSizeColumn(j);
        }
    }

    public static void main(String[] args) {
        String vehicleFile = "./data/车辆信息.xls";
        String vehicleTypeFile = "./data/车型信息.xls";
        String ordersFile = "./data/订单信息.xls:";
        String ordersGroupsFile = "./data/订单信息分组.xls";
        String vehicleGroupsFile = "./data/车辆信息分组.xls";

        try {
            InputStream in = new FileInputStream(new File(ordersGroupsFile));
            HSSFWorkbook wb = new HSSFWorkbook(in);

            // 只获取第一组订单的待选车辆
            HSSFSheet sheet = wb.getSheetAt(0);

            HSSFWorkbook vehicleWb = new HSSFWorkbook();
            HSSFSheet vehicleSheet = vehicleWb.createSheet(String.valueOf(0));
            groupVehicle(vehicleFile, vehicleTypeFile, sheet, vehicleSheet);

            OutputStream out = new FileOutputStream(new File(vehicleGroupsFile));
            vehicleWb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
