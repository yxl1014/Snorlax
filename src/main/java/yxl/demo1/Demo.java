package yxl.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *        版权声明：本文为CSDN博主「yihan928」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 *         原文链接：https://blog.csdn.net/yihan928/article/details/122071896
 */
public class Demo {
    public static void main(String[] args) {
        getData("/home/yxl/图片/微信图片_20220621191356.jpg");
    }

    public static void getGps(String fileName){
        File file = new File(fileName);
        JSONObject jsonObject = getPosition(file);
        Map map = getAddress(jsonObject);

        String gps_longitude = jsonObject.getString("GPS Longitude");//经度
        String gps_latitude = jsonObject.getString("GPS Latitude");//纬度
        double longitude = transformPosition(gps_longitude);
        double latitude = transformPosition(gps_latitude);
        System.out.println("经度："+longitude+"   纬度："+latitude);
        System.out.println("手机型号："+jsonObject.getString("Make")+"  "+jsonObject.getString("Model"));
        System.out.println("拍摄时间："+jsonObject.getString("Date/Time"));
        System.out.println("拍摄地址："+map.get("formatted_address")+" "+map.get("sematic_description"));
    }


    public static void getData(String fileName) {
        File file = new File(fileName);
        JSONObject jsonObject = getPosition(file);
        for (String s : jsonObject.keySet()) {
            System.out.println(s+"=> "+jsonObject.get(s));
        }
    }
    private static JSONObject getPosition(File file) {
        JSONObject jsonObject = new JSONObject();
        Metadata metadata = null;
        try {
            metadata = JpegMetadataReader.readMetadata(file);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    jsonObject.put(tag.getTagName(),tag.getDescription());
                }
            }
        } catch (JpegProcessingException | IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * 经纬度转换
     * @param s
     * @return
     */
    private static double transformPosition(String s) {
        String a = s.split("°")[0].replace(" ", "");
        String b = s.split("°")[1].split("'")[0].replace(" ", "");
        String c = s.split("°")[1].split("'")[1].replace(" ", "").replace("\"", "");
        double gps_dou = Double.parseDouble(a)+Double.parseDouble(b)/60 + Double.parseDouble(c)/60/60;
        return gps_dou;
    }
    /**
     *  经纬度定位结果（百度地图）
     * @return
     */
    private static Map getAddress(JSONObject json) {
        String log = String.valueOf(transformPosition(json.getString("GPS Longitude")));//经度
        String lat = String.valueOf(transformPosition(json.getString("GPS Latitude")));//纬度
        String ak="YNxcSCAphFvuPD4LwcgWXwC3SEZZc7Ra";
        String urlString =
                "http://api.map.baidu.com/reverse_geocoding/v3/?ak=" +ak+
                        "&output=json&coordtype=bd09ll&extensions_road&extensions_poi=1&radius=500&location="+lat+","+log;
        Map map = new LinkedHashMap();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if (conn!=null) {
                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String data = null;
                if ((data = bufferedReader.readLine()) != null) {
                    JSONObject jsonObject = JSON.parseObject(data);
                    Object result = jsonObject.get("result");
                    map = JSONObject.parseObject(JSONObject.toJSONString(result), Map.class);
                }
                inputStreamReader.close();
            }
        } catch (Exception e) {
            System.out.println("error in wapaction,and e is " + e.getMessage());
        }
        return map;
    }
}
