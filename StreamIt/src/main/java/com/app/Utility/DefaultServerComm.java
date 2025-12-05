package com.app.Utility;

import com.app.Identification.ServerIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.Socket;

public class DefaultServerComm {
    private static final Logger log = LogManager.getLogger(DefaultServerComm.class);

    private static final String Host = ServerIdentification.GetHost();
    private static final int Port = ServerIdentification.GetPort();

    public static Socket Connect () {
        Socket socket;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(Host, Port), 200);
        } catch (Exception e) {
            log.error("Unable to start communication with server");
            return null;
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
