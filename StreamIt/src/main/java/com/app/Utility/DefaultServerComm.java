package com.app.Utility;

import com.app.Identification.ServerIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class DefaultServerComm {
    private static final Logger log = LogManager.getLogger(DefaultServerComm.class);

    private static final String Host = ServerIdentification.GetHost();
    private static final int Port = ServerIdentification.GetPort();

    public static SSLSocket Connect () {

        SSLSocket socket;
        try {
            System.setProperty("javax.net.ssl.trustStore", "TrustedStreamItStore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "ClientPassword"); // :)
            System.setProperty("jdk.tls.client.protocols", "TLSv1.3");
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) sslSocketFactory.createSocket(Host, Port);
            socket.startHandshake();
        } catch (Exception e) {
            log.error("Unable to start communication with server");
            return null;
        }
        return socket;
    }

    public static void SocketClose(SSLSocket socket) {
        try{
            socket.close();
        } catch (Exception e){
            log.error("Unable to shut down server Comm.");
        }
    }
}
