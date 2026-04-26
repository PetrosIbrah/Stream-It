package com.app.Utility.ServerCommunication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class CategoriesServerComm {
    private static final Logger log = LogManager.getLogger(CategoriesServerComm.class);

    public static void RequestMoviesOrShows (SSLSocket socket, String Stage) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Stage);
        } catch (IOException e) {
            String msg = "Unable to request " + Stage + " from server.";
            log.error(msg);
        }
    }

    public static String[] GetAllMoviesOrShows(SSLSocket socket) {
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
