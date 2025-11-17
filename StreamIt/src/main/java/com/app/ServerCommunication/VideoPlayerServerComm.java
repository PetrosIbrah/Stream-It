package com.app.ServerCommunication;

import com.app.Identification.ServerIdentification;

import java.io.*;
import java.net.Socket;

public class VideoPlayerServerComm {
    private static final String Host = ServerIdentification.GetHost();
    private static final int Port = ServerIdentification.GetPort();

    public static Socket Connect () {
        Socket socket = null;
        try {
            socket = new Socket(Host, Port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static void SendTimeStamp(Socket socket, String Stage, long TimeStamp){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(TimeStamp);
            System.out.println(TimeStamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReceiveRestart(Socket socket) {
        String Restart = " ";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Restart = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Restart;
    }

    public static void SendAdaptive(Socket socket, String Stage, long TimeStamp, double Speedtest){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(TimeStamp);
            out.println(Speedtest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String GetAssurance(Socket socket) {
        try{
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            return br.readLine();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void SocketClose(Socket socket) {
        try{
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
