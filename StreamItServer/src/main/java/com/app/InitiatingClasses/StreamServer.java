package com.app.InitiatingClasses;

import com.app.AccountUsage.AccountSettings;
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
import javax.net.ssl.*;
import java.io.*;

public class StreamServer {
    private static final Logger log = LogManager.getLogger(StreamServer.class);
    private String Choice;
    private double Speed;
    private String Streamble;
    private String Username;
    private String Email;
    private String Password;
    private String ToChange;
    private String Item;

    public void StartSever (int Port) {
        String password = System.getenv("KEYSTORE_PASSWORD");
        System.setProperty("javax.net.ssl.keyStore", "Encryption/StreamItKeyStore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", password);

        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Port);
            serverSocket.setNeedClientAuth(false);
            String msg = "Server listening on port " + Port;
            log.info(msg);

            for(;;) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                log.info("Client Connected.");

                new Thread(() -> {
                    StreamServer clientServer = new StreamServer();
                    clientServer.handleClient(socket);
                }).start();
            }
        } catch (Exception e) {
            log.fatal("Failed to start server");
        }
    }

    private void handleClient(SSLSocket socket) {
        String StageChoice;
        StageChoice = ReceiveStageChoice(socket);
        String msg = "Client requested stage choice of `" + StageChoice + "`";
        log.info(msg);

        ChooseFunction(StageChoice, socket);
        if (!StageChoice.equals("Adaptive") && !StageChoice.equals("LoadingBar")){
            try {
                socket.close();
            } catch (Exception e) {
                log.fatal("Unable to close communication with client.");
            }
        }
    }

    private String ReceiveStageChoice(SSLSocket socket) {
        String StageChoice;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StageChoice = reader.readLine();
            switch (StageChoice) {
                case "Background", "MediaDetails", "Videos", "StartStream", "LoadingBar" -> {
                    Choice = reader.readLine();
                    if (StageChoice.equals("LoadingBar")) {
                        Streamble = reader.readLine();
                    }
                }
                case "Adaptive" -> {
                    Choice = reader.readLine();
                    String tmpSpeed = reader.readLine();
                    Speed = Double.parseDouble(tmpSpeed);
                    Streamble = reader.readLine();
                }
                case "Log In", "Get All From Library", "Sign Up", "Clear Library", "Delete Account" -> {
                    Username = reader.readLine();
                    Password = reader.readLine();
                    if (StageChoice.equals("Sign Up")){
                        Email = reader.readLine();
                    }
                }
                case "Add To library", "Remove From library" -> {
                    Username = reader.readLine();
                    Password = reader.readLine();
                    Item = reader.readLine();
                }
                case "Change Password", "Change Username" -> {
                    Email = reader.readLine();
                    Password = reader.readLine(); // Old Password || Password
                    ToChange = reader.readLine(); // New Password || New Username
                }
            }
        } catch (Exception e) {
            return "false";
        }
        return StageChoice;
    }

    private void ChooseFunction (String StageChoice, SSLSocket socket) {
        switch (StageChoice) {
            case "Images" -> ImageSender.SendAllImages(socket);
            case "Background" -> {
                BackgroundSender background = new BackgroundSender();
                background.SendBackground(socket, Choice);
            }
            case "MediaDetails" -> {
                MediaDetailsSender mediaDetails = new MediaDetailsSender();
                mediaDetails.SendDetails(socket, Choice);
            }
            case "Videos" -> {
                VideoHandler videoHandler = new VideoHandler();
                videoHandler.SendDetails(socket, Choice);
            }
            case "StartStream" -> {
                StartStream stream = new StartStream();
                stream.Stream(socket, Choice);
            }
            case "LoadingBar" -> {
                StartStream stream = new StartStream();
                stream.UpdateStream(socket, Choice, Streamble, stream.findFreePort());
            }
            case "Adaptive" -> {
                AdaptiveStream adaptive = new AdaptiveStream();
                adaptive.Adapt(socket, Choice, Speed, Streamble);
            }
            case "Sign Up" -> {
                LogInHandler logIn = new LogInHandler();
                logIn.SignUp(socket, Username, Email, Password);
            }
            case "Log In" -> {
                LogInHandler logIn = new LogInHandler();
                logIn.LogIn(socket, Username, Password);
            }
            case "Get All From Library" -> {
                LibraryAccess libraryAccess = new LibraryAccess();
                libraryAccess.ReturnAllLibraryItems(socket, Username, Password);
            }
            case "Add To library" -> {
                LibraryAccess libraryAccess = new LibraryAccess();
                libraryAccess.AddItemToLibrary(socket, Username, Password, Item);
            }
            case "Remove From library" -> {
                LibraryAccess libraryAccess = new LibraryAccess();
                libraryAccess.RemoveFromLibrary(socket, Username, Password, Item);
            }
            case "Clear Library" -> {
                AccountSettings accountSettings = new AccountSettings();
                accountSettings.ClearLibrary(socket, Username, Password);
            }
            case "Delete Account" -> {
                AccountSettings accountSettings = new AccountSettings();
                accountSettings.DeleteAccount(socket, Username, Password);
            }
            case "Change Password" -> {
                AccountSettings accountSettings = new AccountSettings();
                accountSettings.ChangePassword(socket, Email, Password, ToChange);
            }
            case "Change Username" -> {
                AccountSettings accountSettings = new AccountSettings();
                accountSettings.ChangeUsername(socket, Email, Password, ToChange);
            }
            case "Get All Movies" -> MoviesAccess.SendMovies(socket);
            case "Get All Shows" -> ShowsAccess.SendShows(socket);
            case "Get Recommended" -> RecommendedAccess.SendRecommended(socket);
        }

    }
}