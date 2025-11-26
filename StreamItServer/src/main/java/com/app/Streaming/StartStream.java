package com.app.Streaming;

import com.app.Identification.Media;
import com.app.VideoHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class StartStream {
    public static Process Process;
    public static String ClientChoice = null;
    public static void Stream (Socket socket, String Choice) {
        ClientChoice = Choice;
        Media.setStreamingFile(Choice);
        File Streamable = new File( Choice);

        if (Streamable.exists()) {
            TCPStream(Streamable, "0");
        } else {
            System.out.println("Didnt found");
        }


    }

    public static void UpdateStream (Socket socket, String Ms) {
        File Streamable = new File(Media.getStreamingFile());
        StopPlayer();
        if (Streamable.exists()) {
            TCPStream(Streamable, msToSecondsFraction(Ms));
        } else {
            System.out.println("Didnt found");
        }


    }

    public static void StopPlayer() {
        if (Process != null && Process.isAlive()) {
            Process.destroy();
            try {
                Process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Process = null;
    }

    public static void TCPStream(File Streamed, String StartTime) {
        try {
            String ClientIp = InetAddress.getLocalHost().getHostAddress();

            ProcessBuilder Command = new ProcessBuilder( VideoHandler.Getffmpegloc(),
                    "-loglevel", "quiet",
                    "-ss", StartTime,
                    "-i", Streamed.getAbsolutePath(),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", "18",
                    "-b:v", "5000k",
                    "-pix_fmt",
                    "yuv420p",
                    "-f", "mpegts",
                    "tcp://0.0.0.0:7778?listen"
            );
            Command.inheritIO();
            Command.redirectErrorStream(true);
            Process = Command.start();

            System.out.println("Successful TCP Server-end streaming");
            int exitCode = Process.waitFor();
            if (exitCode != 0) {
                System.out.println("Server exited with code: " + exitCode);
            }
        } catch (Exception e) {
            System.out.println("Unsuccessful TCP Server-end streaming: " + e.getMessage());
        }
    }

    public static String msToSecondsFraction(String msString) {
            long ms = Long.parseLong(msString);
            long seconds = ms / 1000;
            long fraction = ms % 1000;
            return seconds + "." + String.format("%03d", fraction);
    }


}
