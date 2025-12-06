package com.app.Streaming;

import com.app.Identification.Media;
import com.app.InitiatingClasses.VideoHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class StartStream {
    private static final Logger log = LogManager.getLogger(StartStream.class);

    public static Process Process;
    public static String ClientChoice = null;
    public static void Stream (Socket ignoredSocket, String Choice) {
        ClientChoice = Choice;
        Media.setStreamingFile(Choice);
        File Streamable = new File( Choice);

        if (Streamable.exists()) {
            TCPStream(Streamable, "0");
        } else {
            log.warn("Didnt find file that client requested.");
        }
    }

    public static void UpdateStream (Socket ignoredSocket, String Ms) {
        File Streamable = new File(Media.getStreamingFile());
        StopPlayer();
        if (Streamable.exists()) {
            TCPStream(Streamable, msToSecondsFraction(Ms));
        } else {
            log.warn("Didnt find file that client requested");
        }
    }

    public static void StopPlayer() {
        if (Process != null && Process.isAlive()) {
            Process.destroy();
            try {
                Process.waitFor();
            } catch (InterruptedException e) {
                log.error("Unable to stop ffmpeg");
            }
        }
        Process = null;
    }

    public static void TCPStream(File Streamed, String StartTime) {
        try {

            ProcessBuilder Command = new ProcessBuilder(
                    VideoHandler.Getffmpegloc(),
                    "-loglevel", "quiet",
                    "-ss", StartTime,
                    "-i", Streamed.getAbsolutePath(),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", "18",
                    "-b:v", "5000k",
                    "-pix_fmt", "yuv420p",
                    "-f", "mpegts",
                    "tcp://0.0.0.0:7778?listen");

            Command.inheritIO();
            Command.redirectErrorStream(true);
            Process = Command.start();

            log.info("Successful TCP Server-end streaming");
            int exitCode = Process.waitFor();
            if (exitCode != 0) {
                String msg = "Server exited with code: " + exitCode;
                log.warn(msg);
            }
        } catch (Exception e) {
            log.fatal("Unsuccessful TCP Server-end streaming.");
        }
    }

    public static String msToSecondsFraction(String msString) {
            long ms = Long.parseLong(msString);
            long seconds = ms / 1000;
            long fraction = ms % 1000;
            return seconds + "." + String.format("%03d", fraction);
    }


}
