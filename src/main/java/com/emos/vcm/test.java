package com.emos.vcm;

import com.emos.vcm.util.ExcelProcess;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


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
        try {
            InputStream in = new FileInputStream("./data/订单.xls");
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);

            System.out.println(ExcelProcess.getTitleIndexOfSheet(sheet, "货物体积(方)"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}