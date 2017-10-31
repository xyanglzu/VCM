package com.emos.vcm;

import com.emos.vcm.model.Model;
import com.emos.vcm.pso.Location;
import com.emos.vcm.pso.ProblemSet;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class ResultAnalysis {
    public static void main(String[] args) {
        try {
            Date a = new Date();
            InputStream vehicleIn = new FileInputStream("./data/车辆.xls");
            InputStream cargoIn = new FileInputStream(new File("./data/订单.xls"));
            HSSFWorkbook vehicleWb = new HSSFWorkbook(vehicleIn);
            HSSFWorkbook cargoWb = new HSSFWorkbook(cargoIn);
            HSSFSheet vehicleSheet = vehicleWb.getSheetAt(0);
            HSSFSheet cargoSheet = cargoWb.getSheetAt(0);

            Model model = new Model(cargoSheet, vehicleSheet, 1);
            model.loadMatrix();

            double[] result = {6, 7, 6, 7, 7, 7, 4, 6, 4, 3, 4, 4, 6, 4, 6, 6, 3, 6, 4, 6, 6, 4, 4, 4, 4, 4, 6, 4, 7};
            Location loc = new Location(result);
            ProblemSet.evaluate(loc, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
