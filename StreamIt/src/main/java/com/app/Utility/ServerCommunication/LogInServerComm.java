package com.app.Utility.ServerCommunication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class LogInServerComm {
    private static final Logger log = LogManager.getLogger(LogInServerComm.class);

    public static void SendLogInAndCredentials(SSLSocket socket, String Stage, String User, String Password){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(User);
            out.println(Password);
        } catch (IOException e) {
            log.error("Unable to send Log In Credentials to server.");
        }
    }

    public static void SendSignUpAndCredentials(SSLSocket socket, String Stage, String User, String Email, String Password){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(User);
            out.println(Password);
            out.println(Email);
            log.info("All sent");
        } catch (IOException e) {
            log.error("Unable to send Sign Up Credentials to server.");
        }
    }

    public static String GetLogInResult(SSLSocket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            log.error("Unable to get login request's result from server.");
        }
        return "Error";
    }
}
