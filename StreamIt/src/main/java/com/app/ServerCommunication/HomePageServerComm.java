package com.app.ServerCommunication;

import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;

import java.io.*;
import java.net.Socket;

public class HomePageServerComm {
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

    public static void SendStageChoice (Socket socket, String Msg){
        try {
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);
            writer.println(Msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int GetImageNumber(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            return dis.readInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void GetAllImages(Socket socket, int ImageCount) {
        try {
            File dir = new File("ReferencePictures");
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    System.out.println("Directory created successfully.");
                } else {
                    System.out.println("Failed to create directory.");
                }
            }

            DataInputStream dis = new DataInputStream(socket.getInputStream());

            for (int i = 0; i < ImageCount; i++) {
                String fileName = dis.readUTF();
                MediaIdentification.AddFile(i, fileName);

                int fileSize = dis.readInt();

                try (FileOutputStream fos = new FileOutputStream(new File(dir, fileName))) {
                    byte[] buffer = new byte[4096];
                    int remaining = fileSize;
                    while (remaining > 0) {
                        int bytesRead = dis.read(buffer, 0, Math.min(buffer.length, remaining));
                        if (bytesRead == -1) throw new IOException("Unexpected end of stream");
                        fos.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }
            }

            System.out.println("All files received successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void SocketClose(Socket socket) {
        try{
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
