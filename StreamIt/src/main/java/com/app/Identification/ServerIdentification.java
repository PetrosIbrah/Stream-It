package com.app.Identification;

import com.app.Utility.CallableFunctions;

public class ServerIdentification {

    private static String UserName;
    private static String Password;

    public static String GetHost () {
        return CallableFunctions.loadServerIp();
    }

    public static int GetPort () {
        return CallableFunctions.loadPort();
    }

    public static String getUserName() {return UserName;}
    public static void setUserName(String userName) {UserName = userName;}

    public static String getPassword() {return Password;}
    public static void setPassword(String password) {Password = password;}
}
