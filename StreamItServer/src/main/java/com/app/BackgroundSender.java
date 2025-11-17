package com.app;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BackgroundSender {

    public static void SendBackground (Socket socket, String Choice) {
        String BackgroundImage = "BackgroundPictures/" + Choice;
        SendBackRoundPicture(socket, BackgroundImage);

    }

    public static void SendBackRoundPicture(Socket socket, String ImageUrl)  {
        File image = new File(ImageUrl);

        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            if (image.isFile()) {
                dos.writeInt((int) image.length());

                FileInputStream fis = new FileInputStream(image);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
            } else {
                System.out.println("Error Finding Image");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }



}
