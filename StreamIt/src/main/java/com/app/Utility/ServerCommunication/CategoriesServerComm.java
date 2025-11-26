package com.app.Utility.ServerCommunication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class CategoriesServerComm {
    private static final Logger log = LogManager.getLogger(CategoriesServerComm.class);

    public static void RequestMoviesOrShows (Socket socket, String Stage) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
        } catch (IOException e) {
            String msg = "Unable to request " + Stage + " from server.";
            log.error(msg);
        }
    }

    public static String[] GetAllMoviesOrShows(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int count = dis.readInt();
            String[] items = new String[count];
            for (int i = 0; i < count; i++) {
                items[i] = dis.readUTF();
            }
            return items;
        } catch (Exception e) {
            log.error("Unable to receive all Movies Or Shows from server.");
            return new String[0];
        }
    }
}
