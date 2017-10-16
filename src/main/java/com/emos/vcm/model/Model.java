package com.emos.vcm.model;

import com.emos.vcm.util.ExcelProcess;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.ejml.simple.SimpleMatrix;


public class Model {
    private int vNum;
    private int cNum;
    private SimpleMatrix vehicle;
    private SimpleMatrix cargo;

    public Model(HSSFSheet ordersGroupSheet, HSSFSheet vehicleGroupSheet) {
        vNum = ExcelProcess.getRealNumberOfRow(vehicleGroupSheet);
        cNum = ExcelProcess.getRealNumberOfRow(ordersGroupSheet);
        vehicle = new SimpleMatrix(vNum, 5);
        int rankIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "排名");
        int vehicleLatIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "车辆位置维度");
        int vehicleLngIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "车辆位置经度");
        int vehicleWeightIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "最大载重");
        int vehicleVolumnIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "最大体积");

//        for (int i = 0; i < vNum; i++) {
//            vehicle.insertIntoThis(i, 0, );
//        }

        cargo = new SimpleMatrix(cNum, 8);
        int cargoVolumnIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "货物体积(方)");
        int cargoWeightIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "货物重量()");
        int pickupTimeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "客户要求提货时间");
        int deliveryTimeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "客户要求到货时间");
        int pickupLatIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址维度");
        int pickupLngIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址经度");
        int deliveryLatIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "收货地址维度");
        int deliveryLngIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "收货地址经度");


    }
}
