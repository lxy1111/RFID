package com.example.device;

import static com.example.device.DeviceApplication.changeAntenna;
import static com.example.device.DeviceApplication.performTask;

/**
 * @Author: lxy
 * @Date: 2020/6/18 12:33
 */
public class AntennaThread extends Thread {

    private String ip;
    private Client conn;
    private SocketClient client;

    public AntennaThread(String ip,Client conn,SocketClient client) {
        this.ip = ip;
        this.conn=conn;
        this.client=client;

    }


    @Override
    public void run() {
        try {

            while(true){

               changeAntenna(client,conn,ip,"00E9");
                Thread.sleep(10);
                changeAntenna(client,conn,ip,"01E8");
                Thread.sleep(10);
                changeAntenna(client,conn,ip,"02E7");
                Thread.sleep(10);
                changeAntenna(client,conn,ip,"03E6");

                Thread.sleep(10);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
