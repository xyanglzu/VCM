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
}
