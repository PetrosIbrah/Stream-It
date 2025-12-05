package com.app.Application.Home;

import com.app.Utility.SceneSwapper;
import com.app.Identification.LibraryIdentification;
import com.app.Identification.ServerIdentification;
import com.app.Utility.DefaultServerComm;
import com.app.Identification.MediaIdentification;
import com.app.Utility.ServerCommunication.HomeServerComm;
import com.app.Utility.ServerCommunication.LibraryServerComm;
import com.app.Utility.ServerCommunication.CategoriesServerComm;
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

    @FXML private Pane ErrorPane;
    @FXML private FlowPane GalleryFlowPane;
    @FXML private AnchorPane rootPane;
    @FXML private Text HomeText;
    @FXML private Pane MenuPane;
    @FXML private Pane ProfilePane;

    private boolean VisibleMenu;
    private boolean VisibleProfile;

    public void InitiazeStartingSceneWith (String Scenename) {
        initialize();
        switch (Scenename) {
            case "Home" -> ClickedOnHome();
            case "Recommended" -> ClickedOnRecommended();
            case "Movies" -> ClickedOnMovies();
            case "Shows" -> ClickedOnShows();
            case "Library" -> ClickedOnLibrary();
        }
        ClickedOnMenu();

    }

    @FXML private void initialize()  {
        VisibleProfile = false;
        VisibleMenu = false;

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
            if (LibraryIdentification.getSavedFileNames() == null) {
                Socket socket = DefaultServerComm.Connect();
                if (socket == null) {
                    ErrorPane.setVisible(true);
                    return;
                }
                LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
                LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
                DefaultServerComm.SocketClose(socket);
            }
            ClickedOnMenu();
            HomeText.setText("Library");
            DisplayImages(LibraryIdentification.getSavedFileNames(), GalleryFlowPane);
        }
    }

    @FXML private void ClickedOnHome() {
        if (!HomeText.getText().equals("Home")) {
            if (MediaIdentification.GetAllFileNames() == null) {
                Socket socket = DefaultServerComm.Connect();
                if (socket == null) {
                    ErrorPane.setVisible(true);
                    return;
                }

                HomeServerComm.SendStageChoice(socket, "Images");

                int ImageCount = HomeServerComm.GetImageNumber(socket);
                MediaIdentification.Init(ImageCount);
                HomeServerComm.GetAllImages(socket, ImageCount);

                DefaultServerComm.SocketClose(socket);
            }
            ClickedOnMenu();
            HomeText.setText("Home");
            DisplayImages(MediaIdentification.GetAllFileNames(), GalleryFlowPane);
        }
    }

    @FXML private void ClickedOnRecommended() {
        if (!HomeText.getText().equals("Recommended")) {

            if (LibraryIdentification.getRecommended() == null ) {
                Socket socket = DefaultServerComm.Connect();
                if (socket == null) {
                    ErrorPane.setVisible(true);
                    return;
                }
                CategoriesServerComm.RequestMoviesOrShows(socket, "Get Recommended");
                LibraryIdentification.setRecommended(CategoriesServerComm.GetAllMoviesOrShows(socket));
                DefaultServerComm.SocketClose(socket);
            }
            ClickedOnMenu();
            HomeText.setText("Recommened");
            DisplayImages(LibraryIdentification.getRecommended(), GalleryFlowPane);
        }
    }

    @FXML private void ClickedOnMovies() {
        if (!HomeText.getText().equals("Movies")) {
            if (LibraryIdentification.getMovies() == null) {
                Socket socket = DefaultServerComm.Connect();
                if (socket == null) {
                    ErrorPane.setVisible(true);
                    return;
                }
                CategoriesServerComm.RequestMoviesOrShows(socket, "Get All Movies");
                LibraryIdentification.setMovies(CategoriesServerComm.GetAllMoviesOrShows(socket));
                DefaultServerComm.SocketClose(socket);
            }
            ClickedOnMenu();
            HomeText.setText("Movies");
            DisplayImages(LibraryIdentification.getMovies(), GalleryFlowPane);
        }
    }

    @FXML private void ClickedOnShows() {
        if (!HomeText.getText().equals("Shows")) {
            if (LibraryIdentification.getShows() == null) {
                Socket socket = DefaultServerComm.Connect();
                if (socket == null) {
                    ErrorPane.setVisible(true);
                    return;
                }
                CategoriesServerComm.RequestMoviesOrShows(socket, "Get All Shows");
                LibraryIdentification.setShows(CategoriesServerComm.GetAllMoviesOrShows(socket));
                DefaultServerComm.SocketClose(socket);
            }
            ClickedOnMenu();
            HomeText.setText("Shows");
            DisplayImages(LibraryIdentification.getShows(), GalleryFlowPane);
        }
    }

    @FXML private void ClickedOnRecordings() {
        SceneSwapper.switchToRecordings(rootPane);
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
    }

    @FXML private void CloseErrorPane () {
        ErrorPane.setVisible(false);
    }
}