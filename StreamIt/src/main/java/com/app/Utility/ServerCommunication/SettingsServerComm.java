package com.app.Utility.ServerCommunication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class SettingsServerComm {
    private static final Logger log = LogManager.getLogger(SettingsServerComm.class);

    public static void RequestClearLibrary (SSLSocket socket, String Stage, String Username, String Password) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Username);
            out.println(Password);
        } catch (Exception e) {
            log.error("Unable to Request library clear");
        }
    }

    public static void RequestDeleteAccount (SSLSocket socket, String Stage, String Username, String Password) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Username);
            out.println(Password);
        } catch (Exception e) {
            log.error("Unable to Delete Account");
        }
    }


    public static String SettingsResult (SSLSocket socket) {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            log.error("Unable to receive Clear library result from server.");
        }
        return null;
    }

    public static void RequestChangePassword (SSLSocket socket, String Stage, String Email, String oldPassword, String newPassword) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Email);
            out.println(oldPassword);
            out.println(newPassword);
        } catch (Exception e) {
            log.error("Unable to change Password");
        }
    }

    public static void RequestChangeUsername (SSLSocket socket, String Stage, String Email, String oldPassword, String newPassword) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Email);
            out.println(oldPassword);
            out.println(newPassword);
        } catch (Exception e) {
            log.error("Unable to change Username");
        }
    }


}
