package com.app.ServerCommunication;

import com.app.Identification.ServerIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ShowsAndMoviesServerComm {
    private static final Logger log = LogManager.getLogger(ShowsAndMoviesServerComm.class);

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

    public static void SocketClose(Socket socket) {
        try{
            socket.close();
        } catch (Exception e){
            log.error("Unable to shut down server Comm.");
        }
    }
}
