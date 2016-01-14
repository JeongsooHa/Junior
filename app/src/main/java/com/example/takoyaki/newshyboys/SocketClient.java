package com.example.takoyaki.newshyboys;

import android.os.Handler;
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
    String roomcode;
    Handler hMain;

    public SocketClient(String ip, int port,String roomcode, String inputtext,int SorP,Handler hMain) {
        this.ip = ip;
        this.port = port;
        this.inputtext = inputtext;
        this.SorP = SorP;
        this.roomcode = roomcode;
        this.hMain = hMain;
    }

    public void run() {

        try {
            // 연결후 바로 ReceiveThread 시작
            this.socket = new Socket(ip, port);
            Log.d("debugNull by SC",this.socket.toString());
            sendthread = new SendThread(socket,roomcode,inputtext,SorP);
            sendthread.start();
            if(SorP==1){
                receivethread = new ReceiveThread(socket, roomcode,hMain);
                receivethread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitServer(){
        sendthread.exitSocket();
    }
}

