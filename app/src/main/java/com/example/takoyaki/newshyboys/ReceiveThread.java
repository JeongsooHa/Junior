package com.example.takoyaki.newshyboys;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by hjj12 on 2016-01-12.
 */
public class ReceiveThread extends Thread {
    int studentORprofessor;
    Socket socket;
    DataInputStream input;

    public ReceiveThread(Socket socket) {
        this.socket = socket;
        try {
            input = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
        }
    }

    public void run() {

        try {
            while (input != null) {
                String msg = input.readUTF();
                Log.d("msg_debug",msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
