package com.emos.vcm.pso;
/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is a driver class to execute the PSO process

import com.emos.vcm.model.Model;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

public class PSODriver {
    public static void main(String args[]) {
        try {
            Date a = new Date();
            InputStream vehicleIn = new FileInputStream("./data/车辆.xls");
            InputStream cargoIn = new FileInputStream(new File("./data/订单.xls"));
            HSSFWorkbook vehicleWb = new HSSFWorkbook(vehicleIn);
            HSSFWorkbook cargoWb = new HSSFWorkbook(cargoIn);
            HSSFSheet vehicleSheet = vehicleWb.getSheetAt(0);
            HSSFSheet cargoSheet = cargoWb.getSheetAt(0);

            Model model = new Model(cargoSheet, vehicleSheet, 1);
            model.Matrix();

            Date b = new Date();
            System.out.println((b.getTime() - a.getTime()) / 1000.0);

            new PSOProcess().execute(model);
            Date c = new Date();
            System.out.println((c.getTime() - b.getTime()) / 1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
