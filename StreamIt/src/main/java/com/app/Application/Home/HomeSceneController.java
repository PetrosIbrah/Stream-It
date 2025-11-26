package com.app.Application.Home;

import com.app.Application.SceneSwapper;
import com.app.Identification.LibraryIdentification;
import com.app.Identification.ServerIdentification;
import com.app.ServerCommunication.HomePageServerComm;
import com.app.Identification.MediaIdentification;
import com.app.ServerCommunication.LibraryServerComm;
import com.app.ServerCommunication.ShowsAndMoviesServerComm;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class HomeSceneController {
    private static final Logger log = LogManager.getLogger(HomeSceneController.class);

    @FXML private FlowPane GalleryFlowPane;
    @FXML private AnchorPane rootPane;
    @FXML private Text HomeText;
    @FXML private Pane MenuPane;
    @FXML private Pane ProfilePane;

    private boolean VisibleMenu;
    private boolean VisibleProfile;

    @FXML private void initialize()  {
        VisibleProfile = false;
        VisibleMenu = false;
        Socket socket = HomePageServerComm.Connect();

        HomePageServerComm.SendStageChoice(socket, "Images");

        int ImageCount = HomePageServerComm.GetImageNumber(socket);
        MediaIdentification.Init(ImageCount);
        HomePageServerComm.GetAllImages(socket, ImageCount);
        DisplayImages(MediaIdentification.GetAllFileNames(), GalleryFlowPane);
        HomePageServerComm.SocketClose(socket);

        HomeText.wrappingWidthProperty().bind(rootPane.widthProperty().subtract(200));
    }

    private void DisplayImages(String[] imageFiles, FlowPane container) {
        container.getChildren().clear();

        for (String fileName : imageFiles) {
            File imageFile = new File("ReferencePictures", fileName);
            if (!imageFile.exists()) continue;

            Image img = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(150);
            imageView.setFitHeight(230);
            imageView.setPreserveRatio(false);

            Rectangle clip = new Rectangle(150, 230);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            imageView.setClip(clip);

            Button invisibleButton = new Button();
            invisibleButton.setOpacity(0);
            invisibleButton.setPrefSize(150, 230);
            invisibleButton.setOnAction(e ->
                    SceneSwapper.switchToChoiceDisplay(rootPane, fileName));

            StackPane stack = new StackPane(imageView, invisibleButton);
            stack.setPrefWidth(150);
            stack.setPrefHeight(230);
            stack.getStyleClass().add("media-image");

            container.getChildren().add(stack);
        }
        container.setHgap(20);
        container.setVgap(40);
    }

    @FXML private void ClickedOnMenu() {
        if (!VisibleMenu){
            MenuPane.setVisible(true);
            VisibleMenu = true;
            ProfilePane.setVisible(false);
            VisibleProfile = false;
        } else {
            MenuPane.setVisible(false);
            VisibleMenu = false;
        }
    }

    @FXML private void ClickedOnProfile() {
        if (!VisibleProfile){
            ProfilePane.setVisible(true);
            VisibleProfile = true;
            MenuPane.setVisible(false);
            VisibleMenu = false;
        } else {
            ProfilePane.setVisible(false);
            VisibleProfile = false;
        }
    }

    @FXML private void ClickedOnLibrary() {
        if (!HomeText.getText().equals("Library")) {
            ClickedOnMenu();
            HomeText.setText("Library");
            if (LibraryIdentification.getSavedFileNames() == null) {
                Socket socket = LibraryServerComm.Connect();
                LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
                LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
                LibraryServerComm.SocketClose(socket);
            }
            DisplayImages(LibraryIdentification.getSavedFileNames(), GalleryFlowPane);
        }
        // SceneSwapper.switchToLibraryScene(rootPane);
    }

    @FXML private void ClickedOnHome() {
        if (!HomeText.getText().equals("Home")) {
            ClickedOnMenu();
            HomeText.setText("Home");

            if (MediaIdentification.GetAllFileNames() == null) {
                Socket socket = HomePageServerComm.Connect();

                HomePageServerComm.SendStageChoice(socket, "Images");

                int ImageCount = HomePageServerComm.GetImageNumber(socket);
                MediaIdentification.Init(ImageCount);
                HomePageServerComm.GetAllImages(socket, ImageCount);

                HomePageServerComm.SocketClose(socket);
            }
            DisplayImages(MediaIdentification.GetAllFileNames(), GalleryFlowPane);
        }
        // SceneSwapper.switchToHomeScene(rootPane);
    }

    @FXML private void ClickedOnRecommended() {
        if (!HomeText.getText().equals("Recommended")) {
            ClickedOnMenu();
            HomeText.setText("Recommened");
            if (LibraryIdentification.getRecommended() == null ) {
                Socket socket = ShowsAndMoviesServerComm.Connect();
                ShowsAndMoviesServerComm.RequestMoviesOrShows(socket, "Get Recommended");
                LibraryIdentification.setRecommended(ShowsAndMoviesServerComm.GetAllMoviesOrShows(socket));
                ShowsAndMoviesServerComm.SocketClose(socket);
            }
            DisplayImages(LibraryIdentification.getRecommended(), GalleryFlowPane);
        }
        // SceneSwapper.switchToRecommendedScene(rootPane);
    }

    @FXML private void ClickedOnMovies() {
        if (!HomeText.getText().equals("Movies")) {
            ClickedOnMenu();
            HomeText.setText("Movies");
            if (LibraryIdentification.getMovies() == null) {
                Socket socket = ShowsAndMoviesServerComm.Connect();
                ShowsAndMoviesServerComm.RequestMoviesOrShows(socket, "Get All Movies");
                LibraryIdentification.setMovies(ShowsAndMoviesServerComm.GetAllMoviesOrShows(socket));
                ShowsAndMoviesServerComm.SocketClose(socket);
            }
            DisplayImages(LibraryIdentification.getMovies(), GalleryFlowPane);
        }
        // SceneSwapper.switchToMoviesScene(rootPane);
    }

    @FXML private void ClickedOnShows() {
        if (!HomeText.getText().equals("Shows")) {
            ClickedOnMenu();
            HomeText.setText("Shows");
            if (LibraryIdentification.getShows() == null) {
                Socket socket = ShowsAndMoviesServerComm.Connect();
                ShowsAndMoviesServerComm.RequestMoviesOrShows(socket, "Get All Shows");
                LibraryIdentification.setShows(ShowsAndMoviesServerComm.GetAllMoviesOrShows(socket));
                ShowsAndMoviesServerComm.SocketClose(socket);
            }
            DisplayImages(LibraryIdentification.getShows(), GalleryFlowPane);
        }
        // SceneSwapper.switchToShowsScene(rootPane);
    }

    @FXML private void ClickedOnLogOut() {
        File rememberFile = new File("rememberme.txt");
        if (!rememberFile.delete()) {
            log.warn("Couldn't delete remember me file.");
        }
        SceneSwapper.switchToLogIn(rootPane);
    }

    @FXML private void ClickedOnExit() {
        SceneSwapper.ShutDownApp();
    }

    @FXML private void ClickedOnSettings() {
        // SceneSwapper.SwapToScene("Template", rootPane);
    }
}