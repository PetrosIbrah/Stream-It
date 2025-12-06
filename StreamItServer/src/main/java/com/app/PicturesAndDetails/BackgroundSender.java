package com.app.PicturesAndDetails;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class BackgroundSender {
    private static final Logger log = LogManager.getLogger(BackgroundSender.class);

    public static void SendBackground (Socket socket, String Choice) {
        String BackgroundImage = "BackgroundPictures/" + Choice;
        SendBackRoundPicture(socket, BackgroundImage);

    }

    private static void SendBackRoundPicture(Socket socket, String ImageUrl)  {
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
                log.error("Unable to find background image");
            }
        } catch (Exception e){
            log.error("Unable to send Background image to client");
        }

    }
}
