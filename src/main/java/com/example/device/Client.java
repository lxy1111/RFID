package com.example.device;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @Author: lxy
 * @Date: 2020/5/21 13:44
 */
public class Client {

    private String POST_URL="http://47.103.83.192:8886/api/rfid/rfidScanOut";

    private String POST_URL2="http://47.103.83.192:8886/api/rfid/rfid";

    private String message;

    public synchronized   void rfid(String equipmentNo,String sampleNo)  {
        try {
            URL postUrl = new URL(this.POST_URL2);
            HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String content = "equipmentNo=" + URLEncoder.encode(equipmentNo, "utf-8") + "&sampleRfidTag=" + URLEncoder.encode(sampleNo, "utf-8");
            out.writeBytes(content);
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
            String line = "";
            while ((line = reader.readLine()) != null) {
                // line = new String(line.getBytes(), "utf-8");
                JSONObject a = new JSONObject(line);
                String msg = (String) a.get("msg");
                this.message = msg;
                System.out.println(msg);
            }
            reader.close();
            connection.disconnect();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public synchronized   void rfidScanOut(String barcode) {
        try {
            URL postUrl = new URL(this.POST_URL);
            HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String content = "sampleRfidTag=" + URLEncoder.encode(barcode, "utf-8");
            out.writeBytes(content);
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
            String line = "";
            while ((line = reader.readLine()) != null) {
                // line = new String(line.getBytes(), "utf-8");
                JSONObject a = new JSONObject(line);
            String msg= (String) a.get("msg");
            this.message=msg;
            System.out.println(msg);
            }
            reader.close();
            connection.disconnect();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }


}
