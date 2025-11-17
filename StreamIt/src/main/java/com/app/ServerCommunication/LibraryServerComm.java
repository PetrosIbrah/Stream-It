package com.app.ServerCommunication;

import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;

import java.io.*;
import java.net.Socket;

public class LibraryServerComm {
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

    public static void RequestFromLibrary (Socket socket, String Stage, String Username, String Password) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Username);
            out.println(Password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void AddToLibrary (Socket socket, String Stage, String Username, String Password, String ToAdd) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Username);
            out.println(Password);
            out.println(ToAdd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] GetAllLibraryItems(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int count = dis.readInt();
            String[] items = new String[count];
            for (int i = 0; i < count; i++) {
                items[i] = dis.readUTF();
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }


    public static void RequestDeleteFromLibrary () {

    }

    public static void ResultDeleteFromLibrary () {

    }

    public static void RequestAddToLibrary () {

    }

    public static String ResultEditLibrary (Socket socket) {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
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
