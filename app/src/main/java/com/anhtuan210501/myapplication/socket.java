package com.anhtuan210501.myapplication;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class socket extends AsyncTask<Void, Void, Void> {
    Socket s;
    DataOutputStream dos;
    PrintWriter pw;
    private String serverAddress;
    private int serverPort;
    private String Data;

    public socket(String serverAddress, int serverPort, String Data) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.Data = Data;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // Tạo kết nối Socket ở đây
            System.out.println("out: " + serverAddress + "   " + serverPort);
            s = new Socket(serverAddress, serverPort);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(Data);
            pw.flush();
            pw.close();
            // Đóng kết nối khi hoàn thành
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
