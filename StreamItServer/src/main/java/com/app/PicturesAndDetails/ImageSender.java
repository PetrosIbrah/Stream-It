package com.app.PicturesAndDetails;

import java.io.*;
import java.net.Socket;

public class ImageSender {
    public static void SendAllImages(Socket socket) {
        String imageFolder = "ReferencePictures/";

        try {
            File folder = new File(imageFolder);
            File[] files = folder.listFiles();

            if (files == null || files.length == 0) {
                System.out.println("No files found in " + imageFolder);
                return;
            }

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(files.length);

            for (File file : files) {
                if (file.isFile()) {
                    dos.writeUTF(file.getName());
                    dos.writeInt((int) file.length());

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
            dos.flush();

            System.out.println("All files sent successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void SendImageNumbers (Socket socket, int FilesNum){
        try {
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);
            String Msg = String.valueOf(FilesNum);
            writer.println(Msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
