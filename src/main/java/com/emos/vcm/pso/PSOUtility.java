package com.emos.vcm.pso;
/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// just a simple utility class to find a minimum position on a list

import org.ejml.simple.SimpleMatrix;

public class PSOUtility {
    public static int getMinPos(double[] list) {
        int pos = 0;
        double minValue = list[0];

        for (int i = 0; i < list.length; i++) {
            if (list[i] < minValue) {
                pos = i;
                minValue = list[i];
            }
        }

        return pos;
    }

    public static SimpleMatrix particleToMatrix(double[] particle, int vNum, int cNum) {
        if (particle.length == cNum) {
            SimpleMatrix result = new SimpleMatrix(vNum, cNum);
            result.zero();

            for (int j = 0; j < cNum; j++) {
                if (Math.floor(particle[j]) != 0) {
                    result.set((int) (Math.floor(particle[j]) - 1), j, 1);
                }
            }
            return result;
        } else {
            return null;
        }
    }
}
