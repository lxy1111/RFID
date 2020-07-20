package com.example.device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


@SpringBootApplication
public class DeviceApplication {



        public static String antID="";

    public static synchronized  byte[] hexStrToByteArray(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }


    public static synchronized  String byteArrayToHexStr(byte[] byteArray) throws Exception {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static synchronized  String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2) + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }

    public static synchronized  boolean verify(String str) {
        try {
            if ((str.substring(0, 4).equals("A00A") || str.substring(0, 4).equals("A004")) && str.substring(6, 8).equals("89")) {
                return true;
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
        return false;
    }

    public static synchronized  String getEpc(String str){
        int len=str.length();
        String epc="";
        try{
         epc=str.substring(14,30);

        }catch (Exception ex){
            System.out.println(ex);
        }
        return epc;
    }

    public static synchronized  String getAntID(String str){
        String antid="";
        try{
            antid=str.substring(8,10);

        }catch (Exception ex){
            System.out.println(ex);
        }
        return antid;
    }





    public static String SendGET(String url){
        String result="";//访问返回结果
        BufferedReader read=null;//读取访问结果
        try {
            //创建url
            URL realurl=new URL(url);
            //打开连接
            URLConnection connection=realurl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //建立连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段，获取到cookies等
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            read = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;//循环读取
            while ((line = read.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(read!=null){//关闭流
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public static synchronized  byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static synchronized void performTask(SocketClient client,Client conn,String ip,String remark ) {
       try {
           String str = "A0040189FFD3";
           byte[] message = hexStrToByteArray(str);
           client.sendMessage(message);
           String ans = byteArrayToHexStr(client.getMessage());
           System.out.println(ip);
           System.out.println(ans);

           if (!verify(ans)) {
               String epc = getEpc(ans);
               String antID = getAntID(ans);
               System.out.println("线程" + ip + "在执行");
               System.out.println(byteArrToBinStr(parseHexStr2Byte(antID)));
               String finalantid = byteArrToBinStr(parseHexStr2Byte(antID)).substring(6, 8);

               String request = ip + finalantid;
               if (remark.equals("大门")) {
                   conn.rfidScanOut(epc);
               } else if (remark.equals("工位")) {
                   conn.rfid(request, epc);
               }
               System.out.println(epc);
           }
       }catch (Exception ex){
           System.out.println(ex);
       }
    }

    public static synchronized void  changeAntenna(SocketClient client,Client conn,String ip ,String antenna) throws Exception{
        String str = "A004FF74"+antenna;
        System.out.println(str);
        byte[] message = hexStrToByteArray(str);
        client.sendMessage(message);
        String ans = byteArrayToHexStr(client.getMessage());
        System.out.println(ip);
        System.out.println(ans);

    }


    public static void main(String[] args) throws Exception {

        SpringApplication.run(DeviceApplication.class, args);

        String url="http://47.103.83.192:8886/api/system_rfid";


        String str2=SendGET(url);

        JSONObject jsonObject=new JSONObject(str2);


        JSONArray datas= (JSONArray) jsonObject.get("datas");


        List<String> ipList=new ArrayList<>();
        List<String> remarkList=new ArrayList<>();
        List<Integer > idList=new ArrayList<>();

        for(int i=0;i<datas.length();i++){
            JSONObject detail= (JSONObject) datas.get(i);
            String ip=detail.get("addr").toString();
            String remark=detail.get("remark").toString();
            Integer id=Integer.parseInt(detail.get("id").toString());
            ipList.add(ip);
            remarkList.add(remark);
            idList.add(id);
        }


        System.out.println(ipList);
        System.out.println(remarkList);

        for(int i=0;i<ipList.size();i++){
             String ip=ipList.get(i);
            String remark=remarkList.get(i);
            int id=idList.get(i);

            Client conn = new Client();
            SocketClient client = new SocketClient();
            client.ClientOpen(ip,id);

            Thread thread=new MyThread(ip,conn,client,remark);
            Thread thread1=new AntennaThread(ip,conn,client);
            thread.start();
            thread1.start();
        }


      //  String ip="192.168.1.120";
//
//        //ExecutorService executor = Executors.newFixedThreadPool(3);
//
//       Client conn = new Client();
//        SocketClient client = new SocketClient();
//        client.ClientOpen(ip);
//
//       Thread thread=new MyThread(ip,conn,client);
//
//
//
//        Client conn2=new Client();
//        SocketClient client2=new SocketClient();
//        client2.ClientOpen("10.0.0.10");
//        Thread thread2=new  MyThread("10.0.0.10",conn2,client2);

//        thread.start();
        //thread2.start();

      //  Thread thread1=new AntennaThread(ip,conn,client);
       // Thread thread3=new AntennaThread("10.0.0.10",conn2,client2);
       //thread1.start();
        //thread3.start();


        }

}
