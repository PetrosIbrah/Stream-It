package com.app.Utility.ServerCommunication;

import com.app.Identification.FileIdentification;
import com.app.Identification.MediaIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocket;
import java.io.*;
// import java.net.Socket;

public class HomeServerComm {
    private static final Logger log = LogManager.getLogger(HomeServerComm.class);

    public static void SendStageChoice (SSLSocket socket, String Msg){
        try {
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);
            writer.println(Msg);
        } catch (IOException e) {
            log.error("Unable to send stage choice");
        }
    }

    public static int GetImageNumber(SSLSocket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            return dis.readInt();
        } catch (Exception e) {
            log.error("Unable to read the amount of images.");
            return 0;
        }
    }

    public static void GetAllImages(SSLSocket socket, int ImageCount) {
        try {
            File dir = new File(FileIdentification.ReferencePictures);
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    log.info("Directory ReferencePictures created successfully.");
                } else {
                    log.warn("Failed to create directory ReferencePictures.");
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

            log.info("All media thumbnails received successfully.");
        } catch (Exception e) {
            log.error("Unable to receive the thumbnails.");
        }
    }
}
