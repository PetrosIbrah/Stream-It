package com.app.Streaming;

import com.app.Identification.Media;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class AdaptiveStream {
    private static final Logger log = LogManager.getLogger(AdaptiveStream.class);

    public static void Adapt (Socket socket, String Ms, double Speed) {
        String Restart = "No";
        double kb = Speed * 1000;
        String Resolution = getResolution(Media.getStreamingFile());
        String OldResolution = Resolution;
        switch (Resolution) {
            case "240p" -> {
                if (kb > 700) {
                    Resolution = "360p";
                }
            }
            case "360p" -> {
                if (kb > 1000) {
                    Resolution = "480p";
                } else if (kb < 750) {
                    Resolution = "240p";
                }
            }
            case "480p" -> {
                if (kb > 2000) {
                    Resolution = "720p";
                } else if (kb < 1000) {
                    Resolution = "360p";
                }
            }
            case "720p" -> {
                if (kb > 4000) {
                    Resolution = "1080p";
                } else if (kb < 2500) {
                    Resolution = "480p";
                }
            }
            case "1080p" -> {
                if (kb < 4500) {
                    Resolution = "480p";
                }
            }
            case null, default -> {}
        }

        assert OldResolution != null;
        if (!OldResolution.equals(Resolution)) {
            String NewVideo = replaceResolution(Media.getStreamingFile(), Resolution);
            Media.setStreamingFile(NewVideo);
            new Thread(() -> StartStream.UpdateStream(socket, Ms)).start();
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
            log.error("Unable to send message to client to restart stream");
        }
    }
}
