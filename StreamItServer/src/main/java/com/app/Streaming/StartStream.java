package com.app.Streaming;

import com.app.InitiatingClasses.VideoHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StartStream {
    private static final Logger log = LogManager.getLogger(StartStream.class);

    private static final int FirstPort = 7778;
    private static final int LastPort = 7780;
    private static final Set<Integer> usedPorts = ConcurrentHashMap.newKeySet();

    public int Port;
    public Process Process;
    public String ClientChoice = null;

    public void Stream (SSLSocket ignoredSocket, String Choice) {
        try {
            PrintWriter out = new PrintWriter(ignoredSocket.getOutputStream(), true);
            out.println("Ok");
            Port = findFreePort();
            out.println(Port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ClientChoice = Choice;
        File Streamable = new File( Choice);

        if (Streamable.exists()) {
            TCPStream(Streamable, "0");
        } else {
            log.warn("Didnt find file that client requested.");
        }
    }

    public void UpdateStream (SSLSocket Socket, String Ms, String Streamble, int newPort) {
        File Streamable = new File(Streamble);
        StopPlayer();
        usedPorts.remove(Port);
        try {
            PrintWriter out = new PrintWriter(Socket.getOutputStream(), true);
            Port = newPort;
            out.println(newPort);
            Socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (Streamable.exists()) {
            TCPStream(Streamable, msToSecondsFraction(Ms));
        } else {
            log.warn("Didnt find file that client requested");
        }
    }

    public void StopPlayer() {
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

    public void TCPStream(File Streamed, String StartTime) {
        try {
            ProcessBuilder Command = new ProcessBuilder(
                    VideoHandler.Getffmpegloc(),
                    "-loglevel", "verbose",
                    "-ss", StartTime,
                    "-i", Streamed.getAbsolutePath(),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", "18",
                    "-b:v", "5000k",
                    "-pix_fmt", "yuv420p",
                    "-f", "mpegts",
                    "tcp://0.0.0.0:" + Port + "?listen");
            // Command.inheritIO();
            Command.redirectErrorStream(true);
            Process = Command.start();


            log.info("Waiting for client to connect on port {}", Port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("frame=") && !line.contains("frame=    0")) {

                    log.info("Client connected to port {}", Port);
                    usedPorts.remove(Port);
                    break;
                }
            }

            log.info("Successful TCP Server-end streaming");
            int exitCode = Process.waitFor();
            usedPorts.remove(Port);
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

    public int findFreePort() {
        for (int port = FirstPort; port <= LastPort; port++) {
            if (usedPorts.add(port)) {
                log.info("Allocated port {} | Used ports: {}", port, usedPorts);
                return port;
            }
        }
        log.warn("No free ports available | Used ports: {}", usedPorts);
        return -1;
    }
}