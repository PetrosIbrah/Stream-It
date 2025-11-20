package com.app.Identification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;

public class ServerIdentification {
    private static final Logger log = LogManager.getLogger(ServerIdentification.class);

    private static String UserName;
    private static String Password;

    public static String GetHost () {
        return ReadHost();
    }

    public static int GetPort () {
        return ReadPort();
    }

    private static String ReadHost() {

        try (BufferedReader br = new BufferedReader(new FileReader("Server.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ServerIp=")) {
                    return line.split("=")[1].trim();
                }
            }
        } catch (Exception e) {
            log.error("Unable to read Hostname from server file.");
        }
        return null;
    }

    private static int ReadPort() {
        int Port;
        String tmp = "-1";
        try {
            BufferedReader br = new BufferedReader(new FileReader("Server.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Port=")) {
                    tmp = line.split("=")[1].trim();
                }
            }
        } catch (Exception e) {
            log.error("Unable to read port from server file.");
        }
        Port = Integer.parseInt(tmp);
        return Port;
    }

    public static String getUserName() {return UserName;}
    public static void setUserName(String userName) {UserName = userName;}

    public static String getPassword() {return Password;}
    public static void setPassword(String password) {Password = password;}
}
