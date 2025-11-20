package com.app.ServerCommunication;

import com.app.Identification.ServerIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class LogInServerComm {
    private static final Logger log = LogManager.getLogger(LogInServerComm.class);

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

    public static void SendStageAndCredentials(Socket socket, String Stage, String User, String Password){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(User);
            out.println(Password);
        } catch (IOException e) {
            log.error("Unable to send Credentials to server.");
        }
    }

    public static String GetLogInResult(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            log.error("Unable to get login request's result from server.");
        }
        return "Error";
    }

    public static void SocketClose(Socket socket) {
        try{
            socket.close();
        } catch (Exception e){
            log.error("Unable to shut down server Comm.");
        }
    }
}
