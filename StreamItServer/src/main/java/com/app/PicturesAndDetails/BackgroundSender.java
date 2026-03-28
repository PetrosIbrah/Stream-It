package com.app.PicturesAndDetails;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class BackgroundSender {
    private static final Logger log = LogManager.getLogger(BackgroundSender.class);

    public void SendBackground (SSLSocket socket, String Choice) {
        String BackgroundImage = "VideosAndPictures/BackgroundPictures/" + Choice;
        SendBackRoundPicture(socket, BackgroundImage);

    }

    private void SendBackRoundPicture(SSLSocket socket, String imageUrl) {
        File image = new File(imageUrl);
        try {
            if (!image.isFile()) {
                log.error("Unable to find background image: {}", imageUrl);
                return;
            }

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt((int) image.length());

            try (FileInputStream fis = new FileInputStream(image)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }
            dos.flush();

            log.info("Background image sent successfully.");

        } catch (Exception e) {
            log.error("Unable to send background image.");
        }
    }
}