package com.emos.vcm.pso;

// this is the problem to be solved
// to find an x and a y that minimize the function below:
// f(x, y) = (2.8125 - x + x * y^4)^2 + (2.25 - x + x * y^2)^2 + (1.5 - x + x*y)^2
// where 1 <= x <= 4, and -1 <= y <= 1

// you can modify the function depends on your needs
// if your problem space is greater than 2-dimensional space
// you need to introduce a new variable (other than x and y)

import com.emos.vcm.model.Cargo;
import com.emos.vcm.model.Model;
import com.emos.vcm.model.Vehicle;
import com.emos.vcm.util.MatrixProcess;
import org.ejml.simple.SimpleMatrix;

public class ProblemSet {
    public static final double VEL_LOW = -1;
    public static final double VEL_HIGH = 1;
    public static final double ERR_TOLERANCE = 1E-20; // the smaller the tolerance, the more accurate the result,
    // but the number of iteration is increased
    public static final double w1 = 0.8;
    public static final double w2 = 0.2;
    public static final double phi1 = 0.05;  // 距离
    public static final double phi2 = 0.05;  // 等待时间
    public static final double phi3 = 0.2;  // 延误时间
    public static final double phi4 = 0.3;  // 装载率
    public static final double phi5 = 0.4;  // 车辆数量

    // rowNum 与 colNum 是原问题矩阵的维度
    public static double evaluate(Location location, Model model) {
        double result = 0;
        int rowNum = model.getvNum();
        int colNum = model.getcNum();
        // 判断传入的参数是否合理
        if (colNum == location.getLoc().length) {
            SimpleMatrix locationMatrix = PSOUtility.particleToMatrix(location.getLoc(), rowNum, colNum);

            for (int i = 0; i < rowNum; i++) {
                // 车必须可用
                Vehicle vehicle = model.getVehicles().get(i);
                if (!vehicle.isAvailable()) {
                    return 1e9;
                }

                // 必须满足体积约束和载重约束
                double sumWeight = 0;
                double sumVolume = 0;
                for (int j = 0; j < colNum; j++) {
                    Cargo cargo = model.getCargos().get(j);
                    sumWeight += cargo.getMaxWeight() * locationMatrix.get(i, j);
                    sumVolume += cargo.getMaxVolume() * locationMatrix.get(i, j);
                }
                if (sumWeight > vehicle.getMaxWeight() || sumVolume > vehicle.getMaxVolume()) {
                    return 1e9;
                }
            }
            double numOfVehicles = 0;
            for (int i = 0; i < rowNum; i++) {
                SimpleMatrix row = locationMatrix.extractVector(true, i);
                numOfVehicles += row.elementMaxAbs();
            }
            if (numOfVehicles != rowNum && locationMatrix.elementSum() != colNum) {
                return 1e9;
            }
            double numOfVehiclesPart = phi5 * numOfVehicles / rowNum;

//            // 每个货物只能匹配一辆车
//            SimpleMatrix vehicleNum = MatrixProcess.sumByColumn(locationMatrix);
//            for (int j = 0; j < colNum; j++) {
//                if (vehicleNum.get(0, j) > 1) {
//                    return 1e9;
//                }
//            }

            // 距离部分成本，并归一化
            SimpleMatrix L = new SimpleMatrix(rowNum, 1);
            for (int i = 0; i < rowNum; i++) {
                L.set(i, 0, model.getDeliveryDistance().dot(locationMatrix.extractVector(true, i)));
            }
            SimpleMatrix D_molecule = locationMatrix.elementMult(model.getVcDistance());
            SimpleMatrix distanceMatrix = MatrixProcess.elementDiv(MatrixProcess.sumByRow(D_molecule).plus(L), MatrixProcess.sumByRow(locationMatrix));
            double maxDistance = distanceMatrix.elementMaxAbs();
            double minDistance = 1e9;
            for (int i = 0; i < distanceMatrix.numRows(); i++) {
                if (minDistance > distanceMatrix.get(i, 0)) {
                    minDistance = distanceMatrix.get(i, 0);
                }
            }
            double distancePart = phi1 * (distanceMatrix.elementSum() / rowNum - minDistance) / (maxDistance - minDistance + 1);

            // 等待时间部分成本，并归一化
            SimpleMatrix waitTimeMatrix = locationMatrix.elementMult(model.getWaitTime());
            double maxWaitTime = waitTimeMatrix.elementMaxAbs();
            double minWaitTime = 1e9;
            for (int i = 0; i < waitTimeMatrix.numRows(); i++) {
                if (minWaitTime > waitTimeMatrix.get(i, 0)) {
                    minWaitTime = waitTimeMatrix.get(i, 0);
                }
            }
            double waitTimePart = phi2 * (waitTimeMatrix.elementSum() / rowNum - minWaitTime) / (maxWaitTime - minWaitTime + 1);

            // 延误时间部分成本，并归一化
            SimpleMatrix delayTimeMatrix = locationMatrix.elementMult(model.getDelayTime());
            double maxDelayTime = delayTimeMatrix.elementMaxAbs();
            double minDelayTime = 0;
            for (int i = 0; i < delayTimeMatrix.numRows(); i++) {
                if (minDelayTime > delayTimeMatrix.get(i, 0)) {
                    minDelayTime = delayTimeMatrix.get(i, 0);
                }
            }
            double delayTimePart = phi3 * (delayTimeMatrix.elementSum() / rowNum - minDelayTime) / (maxDelayTime - minDelayTime + 1);

            double[] loadingRate = new double[rowNum];
            double loadingRatePart = 0;
            double maxLoading = 0;
            double minLoading = 1e9;
            for (int i = 0; i < rowNum; i++) {
                Vehicle vehicle = model.getVehicles().get(i);
                double P = vehicle.getMaxWeight();
                double Q = vehicle.getMaxVolume();
                double weightRate = 0;
                double volumeRate = 0;

                for (int j = 0; j < colNum; j++) {
                    Cargo cargo = model.getCargos().get(j);
                    weightRate += locationMatrix.get(i, j) * cargo.getMaxWeight();
                    volumeRate += locationMatrix.get(i, j) * cargo.getMaxVolume();
                }

                weightRate /= P;
                volumeRate /= Q;

                // 取重量载重率和体积载重率中最大的，作为车的总载重
                if (weightRate > volumeRate) {
                    loadingRate[i] += weightRate;
                } else {
                    loadingRate[i] += volumeRate;
                }

                // 找到所有车中的最大最小载重率
                if (maxLoading < loadingRate[i]) {
                    maxLoading = loadingRate[i];
                }
                if (minLoading > loadingRate[i]) {
                    minLoading = loadingRate[i];
                }
                loadingRatePart += loadingRate[i];
            }
//            loadingRatePart = (loadingRatePart - minLoading * rowNum) / (maxLoading - minLoading + 1);
            loadingRatePart = phi4 / (loadingRatePart + 1);     // 作为优化函数的一部分，要求越小越好
            result = distancePart + waitTimePart + delayTimePart + loadingRatePart + numOfVehiclesPart;
        }
        return result;
    }

    public static void printParameter(Location location, Model model) {
        double result = 0;
        int rowNum = model.getvNum();
        int colNum = model.getcNum();
        // 判断传入的参数是否合理
        if (colNum == location.getLoc().length) {
            SimpleMatrix locationMatrix = PSOUtility.particleToMatrix(location.getLoc(), rowNum, colNum);

            double numOfVehicles = 0;
            for (int i = 0; i < rowNum; i++) {
                SimpleMatrix row = locationMatrix.extractVector(true, i);
                numOfVehicles += row.elementMaxAbs();
            }
            System.out.println("numOfVehicles: " + numOfVehicles);

            // 距离部分成本
            SimpleMatrix L = new SimpleMatrix(rowNum, 1);
            for (int i = 0; i < rowNum; i++) {
                L.set(i, 0, model.getDeliveryDistance().dot(locationMatrix.extractVector(true, i)));
            }
            SimpleMatrix D_molecule = locationMatrix.elementMult(model.getVcDistance());
            SimpleMatrix distanceMatrix = MatrixProcess.elementDiv(MatrixProcess.sumByRow(D_molecule).plus(L), MatrixProcess.sumByRow(locationMatrix));
            System.out.println("Distance Matrix: ");
            distanceMatrix.transpose().print();

            System.out.println("Loading Rate: ");
            double[] loadingRate = new double[rowNum];
            double loadingRatePart = 0;
            double maxLoading = 0;
            double minLoading = 1e9;
            for (int i = 0; i < rowNum; i++) {
                Vehicle vehicle = model.getVehicles().get(i);
                double P = vehicle.getMaxWeight();
                double Q = vehicle.getMaxVolume();
                double weightRate = 0;
                double volumeRate = 0;

                for (int j = 0; j < colNum; j++) {
                    Cargo cargo = model.getCargos().get(j);
                    weightRate += locationMatrix.get(i, j) * cargo.getMaxWeight();
                    volumeRate += locationMatrix.get(i, j) * cargo.getMaxVolume();
                }

                weightRate /= P;
                volumeRate /= Q;

                // 取重量载重率和体积载重率中最大的，作为车的总载重
                if (weightRate > volumeRate) {
                    loadingRate[i] += weightRate;
                } else {
                    loadingRate[i] += volumeRate;
                }

                System.out.print(loadingRate[i] + " ");
                loadingRatePart += loadingRate[i];
            }
        }
    }
}
