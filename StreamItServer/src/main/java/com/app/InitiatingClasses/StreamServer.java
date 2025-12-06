package com.app.InitiatingClasses;

import com.app.AccountUsage.LibraryAccess;
import com.app.Categories.MoviesAccess;
import com.app.Categories.RecommendedAccess;
import com.app.Categories.ShowsAccess;
import com.app.AccountUsage.LogInHandler;
import com.app.PicturesAndDetails.BackgroundSender;
import com.app.PicturesAndDetails.ImageSender;
import com.app.PicturesAndDetails.MediaDetailsSender;
import com.app.Streaming.AdaptiveStream;
import com.app.Streaming.StartStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class StreamServer {
    private static final Logger log = LogManager.getLogger(StreamServer.class);
    private static String Choice;
    private static double Speed;
    private static String Username;
    private static String Password;

    private static String Item;

    public static void StartSever (int Port) {
        VideoHandler.VideoPopulation();

        try (ServerSocket serverSocket = new ServerSocket(Port)) {
            String msg = "Server listening on port " + Port;
            log.info(msg);

            for(;;) {
                Socket socket = serverSocket.accept();
                log.info("Client Connected.");

                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            log.fatal("Failed to start server");
        }
    }

    private static void handleClient(Socket socket) {
        String StageChoice;
        StageChoice = ReceiveStageChoice(socket);
        String msg = "Client requested stage choice of `" + StageChoice + "`";
        log.info(msg);

        ChooseFunction(StageChoice, socket);
        try {
            socket.close();
        } catch (Exception e) {
            log.fatal("Unable to close communication with client.");
        }
    }

    private static String ReceiveStageChoice(Socket socket) {
        String StageChoice;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StageChoice = reader.readLine();
            switch (StageChoice) {
                case "Background", "MediaDetails", "Videos", "StartStream", "LoadingBar" -> Choice = reader.readLine();
                case "Adaptive" -> {
                    Choice = reader.readLine();
                    String tmpSpeed = reader.readLine();
                    Speed = Double.parseDouble(tmpSpeed);
                }
                case "Log In", "Sign Up", "Get All From Library" -> {
                    Username = reader.readLine();
                    Password = reader.readLine();
                }
                case "Add To library", "Remove From library" -> {
                    Username = reader.readLine();
                    Password = reader.readLine();
                    Item = reader.readLine();
                }
            }
        } catch (Exception e) {
            return "false";
        }
        return StageChoice;
    }

    private static void ChooseFunction (String StageChoice, Socket socket) {
        switch (StageChoice) {
            case "Images" -> ImageSender.SendAllImages(socket);
            case "Background" -> BackgroundSender.SendBackground(socket, Choice);
            case "MediaDetails" -> MediaDetailsSender.SendDetails(socket, Choice);
            case "Videos" -> VideoHandler.SendDetails(socket, Choice);
            case "StartStream" -> StartStream.Stream(socket, Choice);
            case "LoadingBar" -> StartStream.UpdateStream(socket, Choice);
            case "Adaptive" -> AdaptiveStream.Adapt(socket, Choice, Speed);
            case "Sign Up" -> LogInHandler.SignUp(socket, Username, Password);
            case "Log In" -> LogInHandler.LogIn(socket, Username, Password);
            case "Get All From Library" -> LibraryAccess.ReturnAllLibraryItems(socket, Username, Password);
            case "Add To library" -> LibraryAccess.AddItemToLibrary(socket, Username, Password, Item);
            case "Remove From library" -> LibraryAccess.RemoveFromLibrary(socket, Username, Password, Item);
            case "Get All Movies" -> MoviesAccess.SendMovies(socket);
            case "Get All Shows" -> ShowsAccess.SendShows(socket);
            case "Get Recommended" -> RecommendedAccess.SendRecommended(socket);
        }

    }

}
