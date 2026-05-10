package com.app.Streaming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.PrintWriter;

public class AdaptiveStream {
    private static final Logger log = LogManager.getLogger(AdaptiveStream.class);
    public void Adapt (SSLSocket socket, String Ms, double Speed, String Steamable) {
        String NewVideo;
        String Restart = "No";
        double kb = Speed * 1000;
        String Resolution = getResolution(Steamable);
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

        int Port;
        assert OldResolution != null;
        if (!OldResolution.equals(Resolution)){
            StartStream SS = new StartStream();
            Port = SS.findFreePort();
        } else {
            Port = 0;
        }
        if (!OldResolution.equals(Resolution)) {
            NewVideo = replaceResolution(Steamable, Resolution);
            new Thread(() -> {
                StartStream stream = new StartStream();
                stream.UpdateStream(socket, Ms, NewVideo, Port);

            }).start();
            Restart = "Restart";
        } else {
            NewVideo = null;
        }
        SendRestart(socket, Restart, NewVideo, Port);
    }

    public String replaceResolution(String filePath, String newResolution) {
        String currentResolution = getResolution(filePath);

        if (currentResolution == null) {
            return filePath;
        }

        return filePath.replace(currentResolution, newResolution);
    }

    public String getResolution(String filePath) {
        String[] resolutions = {"1080p", "720p", "480p", "360p", "240p"};

        for (String res : resolutions) {
            if (filePath.contains(res)) {
                return res;
            }
        }

        return null;
    }

    public void SendRestart(SSLSocket socket, String Restart, String NewVid, int Port){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Restart);
            if (Restart.equals("Restart")) {
                out.println(NewVid);
                out.println(Port);
            }
        } catch (IOException e) {
            log.error("Unable to send message to client to restart stream");
        }
    }
}