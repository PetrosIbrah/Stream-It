package com.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class AdaptiveStream {
    public static void Adapt (Socket socket, String Ms, double Speed) {
        String Restart = "No";
        double kb = Speed * 1000;
        String Resolution = getResolution(Media.getStreamingFile());
        String OldResolution = Resolution;
        if (Resolution.equals("240p")) {
            if (kb > 700) {
                Resolution = "360p";
            }
        } else if (Resolution.equals("360p")) {
            if (kb > 1000) {
                Resolution = "480p";
            } else if (kb < 750) {
                Resolution = "240p";
            }
        } else if (Resolution.equals("480p")) {
            if (kb > 2000) {
                Resolution = "720p";
            } else if (kb < 1000) {
                Resolution = "360p";
            }
        } else if (Resolution.equals("720p")) {
            if (kb > 4000) {
                Resolution = "1080p";
            } else if (kb < 2500) {
                Resolution = "480p";
            }
        } else if (Resolution.equals("1080p")) {
            if (kb < 4500) {
                Resolution = "480p";
            }
        }

        if (!OldResolution.equals(Resolution)) {
            String NewVideo = replaceResolution(Media.getStreamingFile(), Resolution);
            Media.setStreamingFile(NewVideo);
            new Thread(() -> {
                StartStream.UpdateStream(socket, Ms);
            }).start();
            Restart = "Restart";
        }
        SendRestart(socket, Restart);
    }

    public static String replaceResolution(String filePath, String newResolution) {
        String currentResolution = getResolution(filePath);

        if (currentResolution == null) {
            return filePath;
        }

        return filePath.replace(currentResolution, newResolution);
    }

    public static String getResolution(String filePath) {
        String[] resolutions = {"1080p", "720p", "480p", "360p", "240p"};

        for (String res : resolutions) {
            if (filePath.contains(res)) {
                return res;
            }
        }

        return null;
    }

    public static void SendRestart(Socket socket, String Restart){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Restart);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
