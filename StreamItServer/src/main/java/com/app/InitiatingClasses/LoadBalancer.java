package com.app.InitiatingClasses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {
    private static final Logger log = LogManager.getLogger(LoadBalancer.class);

    private static final int[] SERVER_PORTS = {8001, 8002, 8003, 8004};
    private static final AtomicInteger PortNum = new AtomicInteger(0);

    public static void BalanceLoad(int Port) {
        VideoHandler.VideoPopulation();
        for (int port : SERVER_PORTS) {
            log.info("Launching server on port {}", port);
            new Thread(() -> {
                StreamServer streamServer = new StreamServer();
                streamServer.StartSever(port);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        try {
            String password = System.getenv("KEYSTORE_PASSWORD");
            System.setProperty("javax.net.ssl.trustStore", "Encryption/TrustedStreamItStore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", password);
            System.setProperty("javax.net.ssl.keyStore", "Encryption/StreamItKeyStore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", password);
            System.setProperty("jdk.tls.client.protocols", "TLSv1.3");
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket LoadBalanecrSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Port);
            LoadBalanecrSocket.setNeedClientAuth(false);
            log.info("Initiated Load Balanecr at socket 8000");
            while (true) {
                try {
                    SSLSocket ClientSocket = (SSLSocket) LoadBalanecrSocket.accept();
                    PortNum.getAndUpdate(p -> (p + 1) % SERVER_PORTS.length);
                    log.info("Sent a client to Server with port {}",  SERVER_PORTS[PortNum.get()]);
                    new Thread(() -> ConnectionHandler(ClientSocket)).start();
                } catch (Exception e) {
                    log.error("Couldnt accept client | Load Balanncer");
                }
            }
        } catch (Exception e) {
            log.fatal("Couldnt bind socket with Load Balanncer");
        }
    }

    public static void ConnectionHandler(SSLSocket ClientSocket) {

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket ServerSocket = (SSLSocket) factory.createSocket("0.0.0.0", SERVER_PORTS[PortNum.get()]);
            ServerSocket.startHandshake();

            BufferedReader ReadFromClient = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
            PrintWriter WriteToClient = new PrintWriter(ClientSocket.getOutputStream(), true);

            BufferedReader ReadFromServer = new BufferedReader(new InputStreamReader(ServerSocket.getInputStream()));
            PrintWriter WriteToServer = new PrintWriter(ServerSocket.getOutputStream(), true);

            DataOutputStream dosToClient = new DataOutputStream(ClientSocket.getOutputStream());
            DataInputStream disFromServer = new DataInputStream(ServerSocket.getInputStream());
            // =========================================================================

            String Stage = ReadFromClient.readLine();
            WriteToServer.println(Stage);

            switch (Stage) {
                case "StartStream" -> {
                    WriteToServer.println(ReadFromClient.readLine());
                    WriteToClient.println(ReadFromServer.readLine());
                }
                case "Images" -> {
                    int fileCount = disFromServer.readInt();
                    dosToClient.writeInt(fileCount);
                    for (int i = 0; i < fileCount; i++) {
                        dosToClient.writeUTF(disFromServer.readUTF());

                        int fileSize = disFromServer.readInt();
                        dosToClient.writeInt(fileSize);

                        byte[] buffer = new byte[4096];
                        int totalRead = 0;

                        while (totalRead < fileSize) {
                            int bytesToRead = Math.min(buffer.length, fileSize - totalRead);
                            int bytesRead = disFromServer.read(buffer, 0, bytesToRead);
                            if (bytesRead == -1) {
                                log.error("Unexpected EOF while relaying file");
                            }

                            dosToClient.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }

                        dosToClient.flush();
                    }
                }
                case "Background", "MediaDetails", "Videos", "LoadingBar" -> {
                    WriteToServer.println(ReadFromClient.readLine());
                    if (Stage.equals("LoadingBar")){
                        WriteToServer.println(ReadFromClient.readLine());
                    }

                    switch (Stage) {
                        case "Background" -> {
                            int fileSize = disFromServer.readInt();
                            dosToClient.writeInt(fileSize);

                            byte[] buffer = new byte[4096];
                            int totalRead = 0;

                            while (totalRead < fileSize) {
                                int bytesToRead = Math.min(buffer.length, fileSize - totalRead);
                                int bytesRead = disFromServer.read(buffer, 0, bytesToRead);
                                if (bytesRead == -1) {
                                    log.error("Unexpected EOF while relaying file.");
                                }
                                dosToClient.write(buffer, 0, bytesRead);
                                totalRead += bytesRead;
                            }

                            dosToClient.flush();
                        }
                        case "MediaDetails" -> {
                            WriteToClient.println(ReadFromServer.readLine());

                            String Type = ReadFromServer.readLine();
                            WriteToClient.println(Type);

                            WriteToClient.println(ReadFromServer.readLine());

                            if (Type.equalsIgnoreCase("Series")) {
                                int Seasons = Integer.parseInt(ReadFromServer.readLine());
                                WriteToClient.println(Seasons);
                                for (int i = 0; i < Seasons; i++) {
                                    WriteToClient.println(Integer.parseInt(ReadFromServer.readLine()));
                                }
                            }
                        }
                        case "Videos" -> {
                            int count = Integer.parseInt(ReadFromServer.readLine());
                            WriteToClient.println(count);

                            for (int i = 0; i < count; i++) {
                                WriteToClient.println(ReadFromServer.readLine());
                            }

                            WriteToClient.println(ReadFromServer.readLine());
                        }
                    }
                }
                case "Adaptive" -> {
                    // Choice
                    WriteToServer.println(ReadFromClient.readLine());
                    // Speed
                    WriteToServer.println(ReadFromClient.readLine());
                    // Streamable
                    WriteToServer.println(ReadFromClient.readLine());

                    String Restart = ReadFromServer.readLine();
                    WriteToClient.println(Restart);
                    if (Restart.equals("Restart")){
                        WriteToClient.println(ReadFromServer.readLine());
                    }
                }
                case "Log In", "Sign Up", "Get All From Library" -> {
                    // Username
                    WriteToServer.println(ReadFromClient.readLine());
                    // Password
                    WriteToServer.println(ReadFromClient.readLine());
                    if (Stage.equals("Sign Up")){
                        // Email
                        WriteToServer.println(ReadFromClient.readLine());
                    }
                    if (Stage.equals("Log In") || Stage.equals("Sign Up")){
                        // Response
                        WriteToClient.println(ReadFromServer.readLine());
                    } else {
                        int Length = disFromServer.readInt();
                        dosToClient.writeInt(Length);
                        for (int i = 0; i < Length; i++){
                            dosToClient.writeUTF(disFromServer.readUTF());
                        }
                        dosToClient.flush();
                    }
                }
                case "Add To library", "Remove From library" -> {
                    // Username
                    WriteToServer.println(ReadFromClient.readLine());
                    // Password
                    WriteToServer.println(ReadFromClient.readLine());
                    // Item
                    WriteToServer.println(ReadFromClient.readLine());

                    // Response
                    WriteToClient.println(ReadFromServer.readLine());
                }
                case "Get All Movies", "Get All Shows", "Get Recommended" -> {
                    int Length = disFromServer.readInt();
                    dosToClient.writeInt(Length);
                    for (int i = 0; i < Length; i++){
                        dosToClient.writeUTF(disFromServer.readUTF());
                    }
                    dosToClient.flush();
                }
            }
            ClientSocket.close();

            log.info("Successful communication completion");
        } catch (Exception e) {
            log.error("Unsuccessful communication completion");
        }

    }
}