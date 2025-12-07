package com.app.Utility.ServerCommunication;

import com.app.Identification.MediaIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class VideoPlayerServerComm {
    private static final Logger log = LogManager.getLogger(VideoPlayerServerComm.class);

    public static void SendTimeStamp(SSLSocket socket, String Stage, long TimeStamp){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(TimeStamp);
        } catch (IOException e) {
            log.error("Unable to send media time stamp to server");
        }
    }

    public static String ReceiveRestart(SSLSocket socket) {
        String Restart = " ";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Restart = reader.readLine();
        } catch (Exception e) {
            log.error("Unable to receive restart result from server");
        }
        return Restart;
    }

    public static void SendAdaptive(SSLSocket socket, String Stage, long TimeStamp, double Speedtest){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(TimeStamp);
            out.println(Speedtest);
            out.println(MediaIdentification.getStreamableFile());
        } catch (IOException e) {
            log.error("Unable to send speedtest results and timestamp to server");
        }
    }
}