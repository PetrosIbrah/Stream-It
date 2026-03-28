package com.app.Utility.ServerCommunication;

import com.app.Identification.MediaIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChoiceServerComm {
    private static final Logger log = LogManager.getLogger(ChoiceServerComm.class);

    public static void SendStageChoice(SSLSocket socket, String Stage, String Choice){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Choice);
        } catch (Exception e) {
            log.error("Unable to send stage choice");
        }
    }

    public static void GetBackgroundImage(SSLSocket socket, String Choice) {
        try {
            //InputStream is = socket.getInputStream();
            File dir = new File("BackgroundPictures");

            if (!dir.exists()) {
                boolean created = dir.mkdir();
                if (created) {
                    log.info("Successfully created BackgroundPictures directory.");
                } else {
                    log.warn("Unable to create BackgroundPictures directory.");
                }
            } else {
                log.info("Directory BackgroundPictures already exists.1");
            }

            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int fileSize = dis.readInt();

            FileOutputStream fos = new FileOutputStream(new File(dir, Choice));
            byte[] buffer = new byte[4096];
            int remaining = fileSize;
            while (remaining > 0) {
                int bytesRead = dis.read(buffer, 0, Math.min(buffer.length, remaining));
                if (bytesRead == -1) throw new IOException("Unexpected end of stream");
                fos.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
            log.info("All files received successfully.");
        } catch (Exception e) {
            log.error("Unable to receive the backgroung image.");
        }
    }

    public static void GetDetails(SSLSocket socket) {
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            MediaIdentification.setTitle(in.readLine());
            MediaIdentification.setType(in.readLine());
            MediaIdentification.setDescription(in.readLine());

            List<Integer> seasons = new ArrayList<>();
            if (MediaIdentification.getType().equalsIgnoreCase("Series")) {
                int seasonCount = Integer.parseInt(in.readLine());
                for (int i = 0; i < seasonCount; i++) {
                    seasons.add(Integer.parseInt(in.readLine()));
                }
                MediaIdentification.setSeasons(seasons);
            } else {
                MediaIdentification.setSeasons(seasons);
            }
        } catch (Exception e) {
            log.error("Unable to receive Media details.");
        }
    }

    public static void SendStreamChoice(SSLSocket socket, String Stage, String StreamChoice){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(StreamChoice);
        } catch (IOException e) {
            log.error("Unable to send Stream file choice.");
        }
    }

    public static List<String> ReceiveVideoList (SSLSocket socket){
        List<String> videos = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int count = Integer.parseInt(in.readLine());

            for (int i = 0; i < count; i++) {
                String videoPath = in.readLine();
                if (videoPath != null) {
                    videos.add(videoPath);
                }
            }

            long durationMs = Long.parseLong(in.readLine());
            MediaIdentification.setDuration(durationMs);
        } catch (Exception e) {
            log.error("Unable to receive video list.");
        }
        return videos;
    }


    public static String ReceiveVideoResponse (SSLSocket socket) {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            log.error("Unable to receive Video Player Response.");
        }
        return null;
    }

}
