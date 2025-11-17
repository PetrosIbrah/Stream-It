package com.app.Identification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ServerIdentification {
    private static String UserName;
    private static String Password;

    public static String GetHost () {
        return ReadHost("Server.txt");
    }

    public static int GetPort () {
        return ReadPort("Server.txt");
    }

    public static String ReadHost(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ServerIp=")) {
                    return line.split("=")[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int ReadPort(String filePath) {
        int Port;
        String tmp = "-1";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Port=")) {
                    tmp = line.split("=")[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Port = Integer.parseInt(tmp);
        return Port;
    }

    public static String getUserName() {return UserName;}
    public static void setUserName(String userName) {UserName = userName;}

    public static String getPassword() {return Password;}
    public static void setPassword(String password) {Password = password;}
}
