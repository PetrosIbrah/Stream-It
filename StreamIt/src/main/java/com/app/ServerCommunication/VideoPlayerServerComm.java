package com.app.ServerCommunication;

import com.app.Identification.ServerIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class VideoPlayerServerComm {
    private static final Logger log = LogManager.getLogger(VideoPlayerServerComm.class);

    private static final String Host = ServerIdentification.GetHost();
    private static final int Port = ServerIdentification.GetPort();

    public static Socket Connect () {
        Socket socket = null;
        try {
            socket = new Socket(Host, Port);
        } catch (Exception e) {
            log.error("Unable to start communication with server");
        }
        return socket;
    }

    public static void SendTimeStamp(Socket socket, String Stage, long TimeStamp){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(TimeStamp);
        } catch (IOException e) {
            log.error("Unable to send media time stamp to server");
        }
    }

    public static String ReceiveRestart(Socket socket) {
        String Restart = " ";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Restart = reader.readLine();
        } catch (Exception e) {
            log.error("Unable to receive restart result from server");
        }
        return Restart;
    }

    public static void SendAdaptive(Socket socket, String Stage, long TimeStamp, double Speedtest){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(TimeStamp);
            out.println(Speedtest);
        } catch (IOException e) {
            log.error("Unable to send speedtest results and timestamp to server");
        }
    }

    public static void SocketClose(Socket socket) {
        try{
            socket.close();
        } catch (Exception e){
            log.error("Unable to shut down server Comm.");
        }
    }
}