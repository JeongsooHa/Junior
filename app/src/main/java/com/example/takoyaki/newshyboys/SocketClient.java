package com.example.takoyaki.newshyboys;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by hjj12 on 2016-01-12.
 */
public class SocketClient extends Thread{
    String ip;
    int port;
    Socket socket;
    String inputtext;
    int SorP;
    SendThread sendthread;
    ReceiveThread receivethread;

    public SocketClient(String ip, int port,String inputtext,int SorP) {
        this.ip = ip;
        this.port = port;
        this.inputtext = inputtext;
        this.SorP = SorP;
    }

    public void run() {

        try {
            // 연결후 바로 ReceiveThread 시작
            this.socket = new Socket(ip, port);
            Log.d("debugNull by SC",this.socket.toString());
            sendthread = new SendThread(socket,inputtext,SorP);
            sendthread.start();
            if(SorP==1){
                receivethread = new ReceiveThread(socket);
                receivethread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitServer(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

