package com.app.Application.Home;

import com.app.Application.ChoiceDisplay.ChoiceDisplayController;
import com.app.ServerCommunication.HomePageServerComm;
import com.app.Identification.MediaIdentification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class HomeSceneController {
    // public static String[] fileNames;

    @FXML
    public FlowPane GalleryFlowPane;

    @FXML
    public ImageView Video1;

    @FXML
    public AnchorPane rootPane;

    @FXML
    public Text HomeText;

    @FXML private Pane MenuPane;
    @FXML private Pane ProfilePane;
    private boolean VisibleMenu;

    private boolean VisibleProfile;

    @FXML
    public void initialize()  {
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

    public void switchToLogInScene() throws IOException {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/app/LogIn/LogInScene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/app/LogIn/LogIn.css").toExternalForm());


        stage.setTitle("StreamIt");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToChoiceScene(String filename) throws IOException {
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
    }

    public void displayImages(String[] imageFiles, FlowPane container) {
        container.getChildren().clear();

        for (String fileName : imageFiles) {
            try {
                File dir = new File("ReferencePictures");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File imageFile = new File(dir, fileName);;

                if (!imageFile.exists()) continue;

                Image img = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView(img);

                imageView.setFitWidth(155);
                imageView.setFitHeight(211);
                imageView.setPreserveRatio(true);

                Button invisibleButton = new Button();
                invisibleButton.setOpacity(0);
                invisibleButton.setPrefSize(155, 211);
                invisibleButton.setOnAction(e -> {
                    try {
                        System.out.println("Opening scene with file: " + fileName);
                        switchToChoiceScene(fileName);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                StackPane stack = new StackPane(imageView, invisibleButton);

                container.getChildren().add(stack);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        container.setHgap(25);
        container.setVgap(50);
    }

    public void ClickedOnMenu() {
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

    public void ClickedOnProfile() {
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

    public void switchToLibraryScene() throws IOException {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/app/Application/Menu/Library.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/app/Application/Home/Home.css").toExternalForm());


        stage.setTitle("StreamIt");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}