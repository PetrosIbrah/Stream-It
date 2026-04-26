package com.app.Utility;

import com.app.Identification.ServerIdentification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DefaultServerComm {
    private static final Logger log = LogManager.getLogger(DefaultServerComm.class);

    private static final String Host = ServerIdentification.GetHost();
    private static final int Port = ServerIdentification.GetPort();

    public static SSLSocket Connect () {

        SSLSocket socket;
        try {
            InputStream is = DefaultServerComm.class.getResourceAsStream("/com/app/Keys/TrustedStreamItStore.jks");
            File tempStore = File.createTempFile("TrustedStreamItStore", ".jks");
            tempStore.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(tempStore)) {
                assert is != null;
                is.transferTo(fos);
            }

            System.setProperty("javax.net.ssl.trustStore", tempStore.getAbsolutePath());
            System.setProperty("javax.net.ssl.trustStorePassword", "ClientPassword"); // :)
            System.setProperty("jdk.tls.client.protocols", "TLSv1.3");
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) sslSocketFactory.createSocket(Host, Port);
            socket.startHandshake();
        } catch (Exception e) {
            log.error("Unable to start communication with server", e);
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
