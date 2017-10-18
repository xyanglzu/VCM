package com.emos.vcm.model;

import com.emos.vcm.util.ExcelProcess;
import com.emos.vcm.util.GetInfoByBaidu;
import com.emos.vcm.util.TimeProcess;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.ejml.ops.MatrixIO;
import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


public class Model {
    private int vNum;
    private int cNum;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<Cargo> cargos;
    private SimpleMatrix vcDistance;            // 车与取货点距离矩阵
    private SimpleMatrix deliveryDistance;      // 送货距离矩阵
    private SimpleMatrix waitTime;            // 车到达取货点后等待时间矩阵
    private SimpleMatrix delayTime;          // 车到达送货点后延误时间矩阵


    public Model(HSSFSheet ordersGroupSheet, HSSFSheet vehicleGroupSheet, int i) {
        vNum = ExcelProcess.getRealNumberOfRow(vehicleGroupSheet) - 1;  // 去掉标题
        cNum = ExcelProcess.getRealNumberOfRow(ordersGroupSheet) - 1;   // 去掉标题
        vehicles = new ArrayList<Vehicle>();
        cargos = new ArrayList<Cargo>();
        vcDistance = new SimpleMatrix(vNum, cNum);
        deliveryDistance = new SimpleMatrix(1, cNum);
        waitTime = new SimpleMatrix(vNum, cNum);
        delayTime = new SimpleMatrix(vNum, cNum);

        int rankIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "排名");
        int gpsUpTimeIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "GPS上传时间");
        int vehicleLatIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "车辆位置维度");
        int vehicleLngIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "车辆位置经度");
        int vehicleWeightIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "最大载重");
        int vehicleVolumeIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "最大体积");

        for (int v = 1; v <= vNum; v++) {
            HSSFRow row = vehicleGroupSheet.getRow(v);    // 跳过标题
            Vehicle vehicle = new Vehicle((int) row.getCell(rankIndex).getNumericCellValue(), row.getCell(gpsUpTimeIndex).getDateCellValue(),
                    row.getCell(vehicleLatIndex).getNumericCellValue(), row.getCell(vehicleLngIndex).getNumericCellValue(),
                    row.getCell(vehicleWeightIndex).getNumericCellValue(), row.getCell(vehicleVolumeIndex).getNumericCellValue());
            vehicles.add(vehicle);
        }

        int cargoVolumeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "货物体积(方)");
        int cargoWeightIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "货物重量(T)");
        int pickupTimeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "客户要求提货时间");
        int deliveryTimeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "客户要求到货时间");
        int pickupLatIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址维度");
        int pickupLngIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址经度");
        int deliveryLatIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "收货地址维度");
        int deliveryLngIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "收货地址经度");

        for (int c = 1; c <= cNum; c++) {
            HSSFRow row = ordersGroupSheet.getRow(c);     // 跳过标题
            Cargo cargo = new Cargo(row.getCell(cargoVolumeIndex).getNumericCellValue(), row.getCell(cargoWeightIndex).getNumericCellValue(),
                    row.getCell(pickupTimeIndex).getDateCellValue(), row.getCell(deliveryTimeIndex).getDateCellValue(),
                    row.getCell(pickupLatIndex).getNumericCellValue(), row.getCell(pickupLngIndex).getNumericCellValue(),
                    row.getCell(deliveryLatIndex).getNumericCellValue(), row.getCell(deliveryLngIndex).getNumericCellValue());
            cargos.add(cargo);
        }
    }

    public Model(HSSFSheet ordersGroupSheet, HSSFSheet vehicleGroupSheet) throws Exception {
        vNum = ExcelProcess.getRealNumberOfRow(vehicleGroupSheet) - 1;  // 去掉标题
        cNum = ExcelProcess.getRealNumberOfRow(ordersGroupSheet) - 1;   // 去掉标题
        vehicles = new ArrayList<Vehicle>();
        cargos = new ArrayList<Cargo>();
        vcDistance = new SimpleMatrix(vNum, cNum);
        deliveryDistance = new SimpleMatrix(1, cNum);
        waitTime = new SimpleMatrix(vNum, cNum);
        delayTime = new SimpleMatrix(vNum, cNum);

        int rankIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "排名");
        int gpsUpTimeIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "GPS上传时间");
        int vehicleLatIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "车辆位置维度");
        int vehicleLngIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "车辆位置经度");
        int vehicleWeightIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "最大载重");
        int vehicleVolumeIndex = ExcelProcess.getTitleIndexOfSheet(vehicleGroupSheet, "最大体积");

        for (int v = 1; v <= vNum; v++) {
            HSSFRow row = vehicleGroupSheet.getRow(v);    // 跳过标题
            Vehicle vehicle = new Vehicle((int) row.getCell(rankIndex).getNumericCellValue(), row.getCell(gpsUpTimeIndex).getDateCellValue(),
                    row.getCell(vehicleLatIndex).getNumericCellValue(), row.getCell(vehicleLngIndex).getNumericCellValue(),
                    row.getCell(vehicleWeightIndex).getNumericCellValue(), row.getCell(vehicleVolumeIndex).getNumericCellValue());
            vehicles.add(vehicle);
        }

        int cargoVolumeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "货物体积(方)");
        int cargoWeightIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "货物重量(T)");
        int pickupTimeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "客户要求提货时间");
        int deliveryTimeIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "客户要求到货时间");
        int pickupLatIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址维度");
        int pickupLngIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "发货地址经度");
        int deliveryLatIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "收货地址维度");
        int deliveryLngIndex = ExcelProcess.getTitleIndexOfSheet(ordersGroupSheet, "收货地址经度");

        for (int c = 1; c <= cNum; c++) {
            HSSFRow row = ordersGroupSheet.getRow(c);     // 跳过标题
            Cargo cargo = new Cargo(row.getCell(cargoVolumeIndex).getNumericCellValue(), row.getCell(cargoWeightIndex).getNumericCellValue(),
                    row.getCell(pickupTimeIndex).getDateCellValue(), row.getCell(deliveryTimeIndex).getDateCellValue(),
                    row.getCell(pickupLatIndex).getNumericCellValue(), row.getCell(pickupLngIndex).getNumericCellValue(),
                    row.getCell(deliveryLatIndex).getNumericCellValue(), row.getCell(deliveryLngIndex).getNumericCellValue());
            cargos.add(cargo);
        }

        // 初始化车与取货点距离矩阵
        for (int v = 0; v < vNum; v++) {
            for (int c = 0; c < cNum; c++) {
                Vehicle vehicle = vehicles.get(v);
                Cargo cargo = cargos.get(c);
                vcDistance.set(v, c, GetInfoByBaidu.getTravelDistance(vehicle.getLat(), vehicle.getLng(), cargo.getPickupLat(), cargo.getPickupLng()));
            }
        }

        // 初始化送货距离向量
        for (int c = 0; c < cNum; c++) {
            Cargo cargo = cargos.get(c);
            deliveryDistance.set(0, c, GetInfoByBaidu.getTravelDistance(cargo.getPickupLat(), cargo.getPickupLng(), cargo.getDeliveryLat(), cargo.getDeliveryLng()));
        }

        // 初始化车等待的时间矩阵
        for (int v = 0; v < vNum; v++) {
            for (int c = 0; c < cNum; c++) {
                Vehicle vehicle = vehicles.get(v);
                Cargo cargo = cargos.get(c);
                double time = vcDistance.get(v, c) / vehicle.getSpeed();    // 车从gps位置行驶到取货点所使用的时间
                Date pickupTime = TimeProcess.add(vehicle.getGpsUpTime(), time);    // 车到达取货点的时间
                vehicle.setPickupTime(pickupTime);
                waitTime.set(v, c, TimeProcess.difference(cargo.getPickupTime(), pickupTime));  // 车到达取货点需要等待的时间

                // 若车提前到达取货点，则其出发时间为提货时间，否则就是到达取货点的时间
                if (waitTime.get(v, c) > 0) {
                    vehicle.setLeaveTime(cargo.getPickupTime());
                } else {
                    vehicle.setLeaveTime(pickupTime);
                    waitTime.set(v, c, 0);      // 不是提前到达，则等待时间为0
                }
            }
        }

        // 初始化车延误的时间矩阵
        for (int v = 0; v < vNum; v++) {
            for (int c = 0; c < cNum; c++) {
                Vehicle vehicle = vehicles.get(v);
                Cargo cargo = cargos.get(c);
                double time = deliveryDistance.get(0, c) / vehicle.getSpeed();      // 车从取货点到送货点所使用的时间
                Date deliveryTime = TimeProcess.add(vehicle.getLeaveTime(), time);       // 车到达送货点的时间
                vehicle.setDeliveryTime(deliveryTime);
                delayTime.set(v, c, TimeProcess.difference(deliveryTime, cargo.getDeliveryTime()));     // 车到达送货点延误的时间

                // 没有延误的情况下，延误时间为0
                if (delayTime.get(v, c) < 0) {
                    delayTime.set(v, c, 0);
                }
            }
        }
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public ArrayList<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(ArrayList<Cargo> cargos) {
        this.cargos = cargos;
    }

    public SimpleMatrix getVcDistance() {
        return vcDistance;
    }

    public void setVcDistance(SimpleMatrix vcDistance) {
        this.vcDistance = vcDistance;
    }

    public SimpleMatrix getDeliveryDistance() {
        return deliveryDistance;
    }

    public void setDeliveryDistance(SimpleMatrix deliveryDistance) {
        this.deliveryDistance = deliveryDistance;
    }

    public SimpleMatrix getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(SimpleMatrix waitTime) {
        this.waitTime = waitTime;
    }

    public SimpleMatrix getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(SimpleMatrix delayTime) {
        this.delayTime = delayTime;
    }

    public static void main(String[] args) {
        try {
            Date a = new Date();
            InputStream vehicleIn = new FileInputStream("./data/车辆.xls");
            InputStream cargoIn = new FileInputStream(new File("./data/订单.xls"));
            HSSFWorkbook vehicleWb = new HSSFWorkbook(vehicleIn);
            HSSFWorkbook cargoWb = new HSSFWorkbook(cargoIn);
            HSSFSheet vehicleSheet = vehicleWb.getSheetAt(0);
            HSSFSheet cargoSheet = cargoWb.getSheetAt(0);

            Model model = new Model(cargoSheet, vehicleSheet);
            model.saveMatrix();

            Date b = new Date();

            System.out.println((b.getTime() - a.getTime()) / 1000.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getvNum() {
        return vNum;
    }

    public void setvNum(int vNum) {
        this.vNum = vNum;
    }

    public int getcNum() {
        return cNum;
    }

    public void saveMatrix() throws IOException {
        String pathname1 = "./data/vcDistance.csv";
        vcDistance.saveToFileCSV(pathname1);
        String pathname2 = "./data/deliveryDistance.csv";
        deliveryDistance.saveToFileCSV(pathname2);
        String pathname3 = "./data/waitTime.csv";
        waitTime.saveToFileCSV(pathname3);
        String pathname4 = "./data/delayTime.csv";
        delayTime.saveToFileCSV(pathname4);
    }

    public void loadMatrix() throws IOException {
        String pathname1 = "./data/vcDistance.csv";
        vcDistance = new SimpleMatrix(MatrixIO.loadCSV(pathname1));
        String pathname2 = "./data/deliveryDistance.csv";
        deliveryDistance = new SimpleMatrix(MatrixIO.loadCSV(pathname2));
        String pathname3 = "./data/waitTime.csv";
        waitTime = new SimpleMatrix(MatrixIO.loadCSV(pathname3));
        String pathname4 = "./data/delayTime.csv";
        delayTime = new SimpleMatrix(MatrixIO.loadCSV(pathname4));
    }

    public void setcNum(int cNum) {
        this.cNum = cNum;
    }

}
