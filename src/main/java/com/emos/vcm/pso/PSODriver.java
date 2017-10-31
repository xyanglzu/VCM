package com.emos.vcm.pso;
/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is a driver class to execute the PSO process

import com.emos.vcm.model.Model;
import com.emos.vcm.util.CopySheets;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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

            // 调用API计算各个参数，并保存在文件中
//            Model model = new Model(cargoSheet, vehicleSheet);
//            model.saveMatrix();

            // 若数据不变，则直接从文件中读入
            Model model = new Model(cargoSheet, vehicleSheet, 1);
            model.loadMatrix();

            Date b = new Date();
            System.out.println((b.getTime() - a.getTime()) / 1000.0);

            double[] result = new PSOProcess().execute(model);

            HSSFWorkbook resultWb = new HSSFWorkbook();
            Map<Integer, Vector<Integer>> vc = new HashMap<Integer, Vector<Integer>>();
            for (int c = 0; c < result.length; c++) {
                if (result[c] == 0) {
                    continue;
                } else {
                    Integer v = new Integer((int) (result[c] - 1));
                    if (vc.get(v) == null) {
                        Vector<Integer> tmp = new Vector<Integer>();
                        tmp.add(c);
                        vc.put(v, tmp);
                    } else {
                        vc.get(v).add(c);
                    }
                }
            }

            Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
            for (Integer v : vc.keySet()) {
                HSSFSheet sheet = resultWb.createSheet();
                int rowNum = 0;
                HSSFRow vehicleRow = sheet.createRow(rowNum);
                CopySheets.copyRow(vehicleSheet, sheet, vehicleSheet.getRow(v + 1), vehicleRow, styleMap);

                for (Integer c : vc.get(v)) {
                    rowNum++;
                    HSSFRow row = sheet.createRow(rowNum);
                    CopySheets.copyRow(cargoSheet, sheet, cargoSheet.getRow(c + 1), row, styleMap);
                }

                for (int j = 0; j < sheet.getRow(sheet.getFirstRowNum()).getLastCellNum(); j++) {
                    sheet.autoSizeColumn(j);
                }
            }
            OutputStream out = new FileOutputStream(new File("./data/result.xls"));
            resultWb.write(out);

            Date c = new Date();
            System.out.println((c.getTime() - b.getTime()) / 1000.0);

            // deal with result
//            double[] result = {9.000, 9.000, 9.000, 11.000, 9.000, 9.000, 4.000, 9.000, 8.000, 4.000, 9.000, 9.000, 9.000, 9.000, 11.000, 6.000, 6.000, 8.000, 9.000, 9.000, 8.000, 6.000, 9.000, 4.000, 8.000, 8.000, 8.000, 4.000, 11.000};
//            Location loc = new Location(result);
//            System.out.println(ProblemSet.evaluate(loc, model));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
