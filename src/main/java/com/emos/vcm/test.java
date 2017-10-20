package com.emos.vcm;

import org.ejml.simple.SimpleMatrix;

import java.io.IOException;


public class test {
    public test() {
    }

    public static void main(String[] args) {
//        SimpleMatrix testM = new SimpleMatrix(3,2);
//        double[][] a = new double[3][2];
//        a[0][1] = 10;
//        a[2][1] = 5;
//        SimpleMatrix B = new SimpleMatrix(a);
//        B = B.extractMatrix(0,SimpleMatrix.END,0,2);
//        SimpleMatrix I = SimpleMatrix.identity(10);
//        I.set(10);
//        SimpleMatrix X = new SimpleMatrix(2,10);
//        X.zero();
//
//        SimpleMatrix M = new SimpleMatrix(5,5);
//        M.set(1);
//        SimpleMatrix N = new SimpleMatrix(5,5);
//        N.set(2);
//        SimpleMatrix R = new SimpleMatrix(10,5);
//        R.insertIntoThis(0,0,M.combine(5,0,N));
//
//        SimpleMatrix SZA = SimpleMatrix.identity(10).scale(10);
//
//        System.out.print("test");
//        try {
//            InputStream in = new FileInputStream("./data/订单.xls");
//            HSSFWorkbook wb = new HSSFWorkbook(in);
//            HSSFSheet sheet = wb.getSheetAt(0);
//            int i = ExcelProcess.getTitleIndexOfSheet(sheet, "客户要求提货时间");
//            double a = sheet.getRow(1).getCell(i).getNumericCellValue();
//            System.out.println(1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        try {
//            double a = GetInfoByBaidu.getTravelDistance(40.45, 116.34, 40.34, 116.34);
//            System.out.println(a);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        double[] t = {1,2,3,4,5,6,7,8};
//        int rowNum = 2;
//        int colNum = 4;
//        double[][] tmp = new double[rowNum][colNum];
//        for (int i = 0; i < rowNum; i++) {
//            System.arraycopy(t, i*colNum, tmp[i], 0, colNum);
//        }
//        SimpleMatrix tmpa = new SimpleMatrix(tmp);
//        tmpa.print();

        double[][] t = {{1, 2, 3}, {4, 5, 6}};
        SimpleMatrix a = new SimpleMatrix(t);
        try {
            a.saveToFileCSV("1.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        a.print();
        a.scale(3).print();

    }
}