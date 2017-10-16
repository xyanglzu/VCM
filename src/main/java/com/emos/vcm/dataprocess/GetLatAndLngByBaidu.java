package com.emos.vcm.dataprocess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GetLatAndLngByBaidu {

    /**
     * 返回输入地址的经纬度坐标
     * lng(经度),lat(纬度)
     */
    //1,申请ak（即获取密钥），若无百度账号则首先需要注册百度账号。
    public static final String AK = "xFXLbDh24OBgnGluD8dqgDV2lvbg4pyL";

    public static void main(String args[]) {

        String cnAddress = "四川省凉山州西昌市四川凉山州西昌市安宁镇机场路3段238号";
        Map<String, String> map = GetLatAndLngByBaidu.getLatitude(cnAddress);
        if (null != map) {
            System.out.println(cnAddress + "    经度:" + map.get("lng") + "    纬度:" + map.get("lat"));
        } else {
            System.out.println(cnAddress + "    解析错误");
        }
    }

    public static Map<String, String> getLatitude(String address) {
        try {
            address = URLEncoder.encode(address, "UTF-8");          //将地址转换成utf-8的16进制
            //2, 拼写发送http请求的url，注意需使用第一步申请的ak。
            //3, 接收http请求返回的数据（支持json和xml格式）本次采用json形式
            URL resjson = new URL("http://api.map.baidu.com/geocoder/v2/?address="
                    + address + "&output=json&ak=" + AK);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(resjson.openStream()));
            String res;
            StringBuilder sb = new StringBuilder("");
            while ((res = in.readLine()) != null) {
                sb.append(res.trim());
            }
            in.close();
            String str = sb.toString();
            //System.out.println("return json:"+str);
            Map<String, String> map = null;
            if (str != null) {
                int lngStart = str.indexOf("lng\":");
                int lngEnd = str.indexOf(",\"lat");
                int latEnd = str.indexOf("},\"precise");
                if (lngStart > 0 && lngEnd > 0 && latEnd > 0) {
                    String lng = str.substring(lngStart + 5, lngEnd);
                    String lat = str.substring(lngEnd + 7, latEnd);
                    map = new HashMap<String, String>();
                    map.put("lng", lng);
                    map.put("lat", lat);
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
