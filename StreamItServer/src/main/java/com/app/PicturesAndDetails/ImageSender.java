package com.app.PicturesAndDetails;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class ImageSender {
    private static final Logger log = LogManager.getLogger(ImageSender.class);

    public static void SendAllImages(Socket socket) {
        String imageFolder = "ReferencePictures/";

        try {
            File folder = new File(imageFolder);
            File[] files = folder.listFiles();

            if (files == null || files.length == 0) {
                String msg = "No files found in " + imageFolder;
                log.warn(msg);
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

            log.info("All Main images sent successfully.");

        } catch (Exception e) {
            log.error("Unable to send Main images");
        }
    }
}
