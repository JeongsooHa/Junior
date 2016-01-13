package com.example.takoyaki.newshyboys;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by hjj12 on 2016-01-12.
 */
public class ReceiveThread extends Thread {
    Socket socket;
    DataInputStream input;
    String roomcode;
    Handler hMain;


    public ReceiveThread(Socket socket,String roomcode, Handler hMain) {
        Log.d("debug", "리시브 스레드");
        this.socket = socket;
        this.roomcode = roomcode;
        this.hMain = hMain;
        try {
            input = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
        }
    }

    public void run() {

        try {
            Log.d("debug", "리시브 스레드2");
            while (input != null) {
                String msg = input.readUTF();
                Log.d("msg_debug", msg);
                ProfessorActivity.arrayList.add(msg);
                Log.d("debug", "리시브 스레드3");
                Message please = Message.obtain(hMain,0,0);
                hMain.sendMessage(please);
            }
            Log.d("debug", "리시브 스레드5");
        }catch (IOException e) {
            e.printStackTrace();
        }

    }


}
