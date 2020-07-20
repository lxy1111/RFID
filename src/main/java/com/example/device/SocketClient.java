package com.example.device;

import org.apache.commons.logging.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class SocketClient {

    public  Socket socket = null;
    public  OutputStream outputStream;
    public  InputStream inputStream;
    private String URL="http://47.103.83.192:8886/api/system_rfid";
    private int id;




    public synchronized   void updateEquip(String url,String ip,String status)  {
     try {
         URL postUrl = new URL(url);
         HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
         connection.setDoOutput(true);
         connection.setDoInput(true);
         connection.setRequestMethod("PUT");
         connection.setUseCaches(false);
         connection.setInstanceFollowRedirects(true);
         connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
         connection.setRequestProperty("accept", "application/json");
         connection.setRequestProperty("status", status);

         connection.connect();
         DataOutputStream out = new DataOutputStream(connection.getOutputStream());
         String content = "ip="+URLEncoder.encode(ip, "utf-8");
         out.writeBytes(content);
         out.flush();
         out.close();
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
         String line = "";

         while ((line = reader.readLine()) != null) {
             // line = new String(line.getBytes(), "utf-8");
             JSONObject a = new JSONObject(line);
             String msg = (String) a.get("msg");

             System.out.println(msg);
         }
         reader.close();
         connection.disconnect();
     }catch (Exception ex){
         System.out.println(ex);
     }
    }

    // 要连接的服务端IP地址和端口
    public synchronized void doClientOpen (String host,int port,int id) throws Exception{

        // 与服务端建立连接

        try {
            socket = new Socket(host, port);
            // 开启保持活动状态的套接字
            socket.setKeepAlive(true);
            // 设置读取超时时间
            socket.setSoTimeout(10 * 1000);

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            updateEquip(this.URL,host,"不可用");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            updateEquip(this.URL,host,"不可用");
        }
        // 建立连接后获得输出流
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            updateEquip(this.URL,host,"可用");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            updateEquip(this.URL,host,"不可用");

        }


    }


    public synchronized  void ClientOpen (String ip,int id) throws Exception {
        doClientOpen(ip, 4001,id);
    }


    public synchronized  void sendMessage(byte[] message) {
        try {
            socket.getOutputStream().write(message);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
            String line = "";
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public synchronized  byte[] getMessage() {
        int count = 0;
        while (count == 0) {
            try {
                count = inputStream.available();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        byte[] b = new byte[count];
        try {
            inputStream.read(b);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	/*byte[] bytes = new byte[count];
    	int readCount = 0; // 已经成功读取的字节的个数
    	while (readCount < count) {
    		try {
				readCount += inputStream.read(bytes, readCount, count - readCount);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}*/
        return b;
    }



    public  void close() {
        try {
            inputStream.close();
            inputStream=null;
            outputStream.close();
            outputStream=null;
            socket.close();
            socket=null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
