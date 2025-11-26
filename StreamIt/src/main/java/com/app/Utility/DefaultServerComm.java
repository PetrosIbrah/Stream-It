package com.app.Utility;

import com.app.Identification.ServerIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class DefaultServerComm {
    private static final Logger log = LogManager.getLogger(DefaultServerComm.class);

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

    public static void SocketClose(Socket socket) {
        try{
            socket.close();
        } catch (Exception e){
            log.error("Unable to shut down server Comm.");
        }
    }
}
