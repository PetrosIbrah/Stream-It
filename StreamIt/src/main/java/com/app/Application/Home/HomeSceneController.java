package com.app.Application.Home;

import com.app.Application.ChoiceDisplay.ChoiceDisplayController;
import com.app.Application.MenuAndProfileUtility;
import com.app.ServerCommunication.HomePageServerComm;
import com.app.Identification.MediaIdentification;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
        displayImages(MediaIdentification.GetAllFileNames(), GalleryFlowPane);
        HomePageServerComm.SocketClose(socket);

        HomeText.wrappingWidthProperty().bind(rootPane.widthProperty().subtract(200));
    }

    private void switchToChoiceScene(String filename) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/Application/ChoiceDisplay/ChoiceDisplay.fxml"));
            Parent root = loader.load();

            ChoiceDisplayController controller = loader.getController();

            controller.InitializeData(filename);

            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/com/app/Application/ChoiceDisplay/ChoiceDisplay.css").toExternalForm());

            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setMinWidth(854);
            stage.setMinHeight(480);

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayImages(String[] imageFiles, FlowPane container) {
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
            invisibleButton.setOnAction(e -> switchToChoiceScene(fileName));

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
        MenuAndProfileUtility.switchToLibraryScene(rootPane);
    }

    @FXML private void ClickedOnHome() {
        MenuAndProfileUtility.switchToHomeScene(rootPane);
    }

    @FXML private void ClickedOnLogOut() {
        new File("rememberme.txt").delete();
        MenuAndProfileUtility.switchToLogIn(rootPane);
    }

    @FXML public void ClickedOnExit() {
        MenuAndProfileUtility.ShutDownApp();
    }
}