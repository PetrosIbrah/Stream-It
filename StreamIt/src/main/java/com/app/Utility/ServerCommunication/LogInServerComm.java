package com.app.Utility.ServerCommunication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class LogInServerComm {
    private static final Logger log = LogManager.getLogger(LogInServerComm.class);

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
}
