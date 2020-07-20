package com.example.device;

import static com.example.device.DeviceApplication.*;

/**
 * @Author: lxy
 * @Date: 2020/6/18 10:17
 */
public class MyThread extends Thread{


    private String ip;
    private Client conn;
    private SocketClient client;
    private String remark;

   public MyThread(String ip,Client conn,SocketClient client,String remark) {
        this.ip = ip;
        this.conn=conn;
        this.client=client;
        this.remark=remark;

   }

    @Override
    public void run() {
        try {
//            Client conn = new Client();
//            SocketClient client = new SocketClient();
//            client.ClientOpen(ip);

           while(true) {
//               String str = "A0040189FFD3";
//               byte[] message = hexStrToByteArray(str);
//               client.sendMessage(message);
//               String ans = byteArrayToHexStr(client.getMessage());
//               System.out.println(ip);
//               System.out.println(ans);
//
//               if (!verify(ans)) {
//                   String epc = getEpc(ans);
//                   String antID = getAntID(ans);
//                   System.out.println("线程"+ip+"在执行");
//                   //System.out.println(byteArrToBinStr(parseHexStr2Byte(antID)));
//                   conn.rfidScanOut(epc, "inside");
//                   System.out.println(epc);
//               }
               performTask(client,conn,ip,remark);
               Thread.sleep(10);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
