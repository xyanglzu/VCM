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
    public static final double LOC_LOW = 0;
    public static final double LOC_HIGH = 1;
    public static final double VEL_LOW = -1;
    public static final double VEL_HIGH = 1;
    public static final double ERR_TOLERANCE = 1E-20; // the smaller the tolerance, the more accurate the result,
    // but the number of iteration is increased
    public static final double w1 = 0.5;
    public static final double w2 = 0.5;
    public static final double phi1 = 0.25;
    public static final double phi2 = 0.25;
    public static final double phi3 = 0.25;
    public static final double phi4 = 0.25;

    // rowNum 与 colNum 是原问题矩阵的维度
    public static double evaluate(Location location, Model model) {
        double result = 0;
        int rowNum = model.getvNum();
        int colNum = model.getcNum();
        // 判断传入的参数是否合理
        if (rowNum * colNum == location.getLoc().length) {
            double[][] tmp = new double[rowNum][colNum];
            for (int i = 0; i < rowNum; i++) {
                System.arraycopy(location.getLoc(), i * colNum, tmp[i], 0, colNum);
            }
            SimpleMatrix locationMatrix = new SimpleMatrix(tmp);

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

            // 每个货物只能匹配一辆车
            SimpleMatrix vehicleNum = MatrixProcess.sumByColumn(locationMatrix);
            for (int j = 0; j < colNum; j++) {
                if (vehicleNum.get(0, j) > 1) {
                    return 1e9;
                }
            }


            SimpleMatrix L = new SimpleMatrix(rowNum, 1);
            for (int i = 0; i < rowNum; i++) {
                L.set(i, 0, model.getDeliveryDistance().dot(locationMatrix.extractVector(true, i)));
            }
            SimpleMatrix D_molecule = locationMatrix.elementMult(model.getVcDistance());
            double distancePart = phi1 * MatrixProcess.elementDiv(MatrixProcess.sumByRow(D_molecule).plus(L), MatrixProcess.sumByRow(locationMatrix)).elementSum();

            double waitTimePart = phi2 * locationMatrix.elementMult(model.getWaitTime()).elementSum();

            double delayTimePart = phi3 * locationMatrix.elementMult(model.getDelayTime()).elementSum();

            double loadingRatePart = 0;
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

                if (weightRate > volumeRate) {
                    loadingRatePart += weightRate;
                } else {
                    loadingRatePart += volumeRate;
                }
            }
            loadingRatePart = phi4 / (loadingRatePart + 1);

            double numOfVehicles = 0;
            for (int i = 0; i < rowNum; i++) {
                SimpleMatrix row = locationMatrix.extractVector(true, i);
                numOfVehicles += row.elementMaxAbs();
            }

            result = w1 * (distancePart + waitTimePart + delayTimePart + loadingRatePart) + w2 * numOfVehicles;
        }

        return result;
    }
}
