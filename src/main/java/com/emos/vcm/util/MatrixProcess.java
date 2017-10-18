package com.emos.vcm.util;

import org.ejml.simple.SimpleMatrix;

public class MatrixProcess {
    public static SimpleMatrix sumByRow(SimpleMatrix matrix) {
        int numRows = matrix.numRows();
        SimpleMatrix result = new SimpleMatrix(numRows, 1);
        for (int i = 0; i < numRows; i++) {
            SimpleMatrix rowVector = matrix.extractVector(true, i);
            result.set(i, 0, rowVector.elementSum());
        }
        return result;
    }

    public static SimpleMatrix sumByColumn(SimpleMatrix matrix) {
        int numCols = matrix.numCols();
        SimpleMatrix result = new SimpleMatrix(1, numCols);
        for (int i = 0; i < numCols; i++) {
            SimpleMatrix colVector = matrix.extractVector(false, i);
            result.set(0, i, colVector.elementSum());
        }
        return result;
    }

    public static SimpleMatrix elementDiv(SimpleMatrix matrix1, SimpleMatrix matrix2) {
        if (matrix1.numRows() == matrix2.numRows() && matrix1.numCols() == matrix2.numCols()) {
            int numRow = matrix1.numRows();
            int numCol = matrix1.numCols();
            SimpleMatrix result = new SimpleMatrix(numRow, numCol);

            for (int i = 0; i < numRow; i++) {
                for (int j = 0; j < numCol; j++) {
                    result.set(i, j, matrix1.get(i, j) / matrix2.get(i, j));
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public static SimpleMatrix oneArrayToMatrix(double[] array, int rowNum, int colNum) {
        if (rowNum * colNum == array.length) {
            double[][] tmp = new double[rowNum][colNum];
            for (int i = 0; i < rowNum; i++) {
                System.arraycopy(array, i * colNum, tmp[i], 0, colNum);
            }
            return new SimpleMatrix(tmp);
        } else {
            return null;
        }
    }
}
