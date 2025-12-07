package com.app.Utility.ServerCommunication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class LibraryServerComm {
    private static final Logger log = LogManager.getLogger(LibraryServerComm.class);

    public static void RequestFromLibrary (SSLSocket socket, String Stage, String Username, String Password) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Username);
            out.println(Password);
        } catch (IOException e) {
            log.error("Unable to request user's library from server.");
        }
    }

    public static void AddToLibrary (SSLSocket socket, String Stage, String Username, String Password, String ToAdd) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
            out.println(Username);
            out.println(Password);
            out.println(ToAdd);
        } catch (IOException e) {
            log.error("Unable to add to user's library - Server Comm.");
        }
    }

    public static String[] GetAllLibraryItems(SSLSocket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int count = dis.readInt();
            String[] items = new String[count];
            for (int i = 0; i < count; i++) {
                items[i] = dis.readUTF();
            }
            return items;
        } catch (Exception e) {
            log.error("Unable to receive all user's library from server.");
            return new String[0];
        }
    }

    public static String ResultEditLibrary (SSLSocket socket) {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            log.error("Unable to receive library request result from server.");
        }
        return null;
    }
}
