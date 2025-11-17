package com.app.ServerCommunication;

import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChoiceSceneServerComm {
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

    public static void SendStageChoice(Socket socket, String Stage, String Choice){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            System.out.println(Choice);
            out.println(Choice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void GetBackgroundImage(Socket socket, String Choice) {
        try {
            InputStream is = socket.getInputStream();
            File dir = new File("BackgroundPictures");

            if (!dir.exists()) {
                boolean created = dir.mkdir();
                if (created) {
                    System.out.println("Directory created successfully 2.");
                } else {
                    System.out.println("Failed to create directory.");
                }
            } else {
                System.out.println("Directory already exists.");
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
            fos.close();

            System.out.println("debuging");

            is.close();
            System.out.println("All files received successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void GetDetails(Socket socket) {
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
            e.printStackTrace();
        }
    }

    public static void SendStreamChoice(Socket socket, String Stage, String StreamChoice){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(StreamChoice);
            System.out.println(StreamChoice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> ReceiveVideoList (Socket socket){
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

            System.out.println("Received " + videos.size() + " videos from server.");
            long durationMs = Long.parseLong(in.readLine());
            MediaIdentification.setDuration(durationMs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videos;
    }

    public static void SocketClose(Socket socket) {
        try{
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
