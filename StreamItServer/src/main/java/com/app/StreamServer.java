package com.app;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StreamServer {
    private static String Choice;
    private static double Speed;
    private static String ClientIp;

    private static String Username;
    private static String Password;

    private static String Item;

    public static void StartSever () {
        VideoHandler.VideoPopulation();
        int port = 7777;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");

                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            ClientIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        try {
            String StageChoice;
            StageChoice = ReceiveStageChoice(socket);
            System.out.println(StageChoice);

            ChooseFunction(StageChoice, socket);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String ReceiveStageChoice(Socket socket) {
        String StageChoice = " ";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StageChoice = reader.readLine();
            if (StageChoice.equals("Background") || StageChoice.equals("MediaDetails") || StageChoice.equals("Videos") || StageChoice.equals("StartStream")
                || StageChoice.equals("LoadingBar")) {
                Choice = reader.readLine();
            } else if (StageChoice.equals("Adaptive")) {
                Choice = reader.readLine();
                String tmpSpeed = reader.readLine();
                Speed = Double.parseDouble(tmpSpeed);
            } else if (StageChoice.equals("Log In") || StageChoice.equals("Sign Up") || StageChoice.equals("Get All From Library")){
                Username = reader.readLine();
                Password = reader.readLine();
            } else if (StageChoice.equals("Add To library")){
                Username = reader.readLine();
                Password = reader.readLine();
                Item = reader.readLine();
            }
        } catch (Exception e) {
            return "false";
        }
        return StageChoice;
    }

    public static void ChooseFunction (String StageChoice, Socket socket) {
        if (StageChoice.equals("Images")){
            ImageSender.SendAllImages(socket);
        } else if (StageChoice.equals("Background")) {
            BackgroundSender.SendBackground(socket, Choice);
        } else if (StageChoice.equals("MediaDetails")) {
            MediaDetailsSender.SendDetails(socket, Choice);
        } else if (StageChoice.equals("Videos")) {
            VideoHandler.SendDetails(socket, Choice);
        } else if (StageChoice.equals("StartStream")){
            StartStream.Stream(socket, Choice);
        } else if (StageChoice.equals("LoadingBar")){
            StartStream.UpdateStream(socket, Choice);
        } else if (StageChoice.equals("Adaptive")){
            AdaptiveStream.Adapt(socket, Choice, Speed);
        } else if (StageChoice.equals("Sign Up")) {
            LogInHandler.SignUp(socket, Username, Password);
        } else if (StageChoice.equals("Log In")) {
            LogInHandler.LogIn(socket, Username, Password);
        } else if (StageChoice.equals("Get All From Library")) {
            LibraryAccess.ReturnAllLibraryItems(socket, Username, Password);
        } else if (StageChoice.equals("Add To library")) {
            LibraryAccess.AddItemToLibrary(socket, Username, Password, Item);
        }

    }

}
