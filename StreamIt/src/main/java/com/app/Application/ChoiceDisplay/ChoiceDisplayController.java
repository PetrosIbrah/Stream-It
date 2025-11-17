package com.app.Application.ChoiceDisplay;


import com.app.Application.MenuAndProfileUtility;
import com.app.Identification.LibraryIdentification;
import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;
import com.app.ServerCommunication.ChoiceSceneServerComm;
import com.app.ServerCommunication.LibraryServerComm;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ChoiceDisplayController {
    @FXML
    private ImageView ChoiceImage;
    @FXML
    public Text HomeText;
    @FXML
    public Text textDesc;
    @FXML
    public FlowPane SidePane;
    @FXML
    public HBox pinnedControls;
    @FXML
    public VBox SidePanel;
    @FXML
    public ScrollPane sideScrollPane;
    @FXML
    private ImageView LibraryIcon;
    private Boolean SavedOrNot;

    @FXML
    public AnchorPane rootPane;

    @FXML private TextFlow textflowDesc;



    public void InitializeData(String filename){


        String Choice;
        Choice = filename;
        MediaIdentification.SetChoice(Choice);
        HomeText.setText(formatFileName(filename));
        //System.out.println("You will be saving this for example " + MediaIdentification.GetChoice());
        GetBackground(Choice);
        SetBackgroundImage(Choice);

        String choice = MediaIdentification.GetChoice();
        String[] savedFiles = LibraryIdentification.getSavedFileNames();

        if (Arrays.asList(savedFiles).contains(choice)) {
            Image img = new Image(getClass().getResource("/com/app/Application/ChoiceDisplay/starfull.png").toExternalForm());
            LibraryIcon.setImage(img);
            SavedOrNot = true;
        } else {
            Image img = new Image(getClass().getResource("/com/app/Application/ChoiceDisplay/star.png").toExternalForm());
            LibraryIcon.setImage(img);
            SavedOrNot = false;
        }

        textDesc.setText(MediaIdentification.getDescription());
        if (MediaIdentification.getType().equalsIgnoreCase("Series")) {
            SidePaneHandler.SetUpChoices(SidePane, pinnedControls);
        } else if (MediaIdentification.getType().equalsIgnoreCase("Movie")) {
            Socket socket = ChoiceSceneServerComm.Connect();
            ChoiceSceneServerComm.SendStreamChoice(socket, "Videos", MediaIdentification.getTitle());
            List<String> videolist = ChoiceSceneServerComm.ReceiveVideoList(socket);
            SidePaneHandler.SetUpButtons(videolist, SidePane);
        }

        VBox.setVgrow(sideScrollPane, Priority.ALWAYS);


        ChoiceImage.fitWidthProperty().bind(rootPane.widthProperty().subtract(340));
        ChoiceImage.fitHeightProperty().bind(rootPane.heightProperty().subtract(71));

        textDesc.wrappingWidthProperty().bind(textflowDesc.widthProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(textflowDesc.widthProperty());
        clip.heightProperty().bind(textflowDesc.heightProperty());
        textflowDesc.setClip(clip);

        HomeText.wrappingWidthProperty().bind(rootPane.widthProperty().subtract(200));

    }

    public String formatFileName(String fileName) {
        if (fileName == null) return null;

        String withoutExtension = fileName.replaceFirst("(?i)\\.jpg$", "");

        return withoutExtension.replace("_", " ");
    }

    public void GetBackground(String Choice) {

        Socket socket = ChoiceSceneServerComm.Connect();
        ChoiceSceneServerComm.SendStageChoice(socket, "Background", Choice);
        ChoiceSceneServerComm.GetBackgroundImage(socket, Choice);
        ChoiceSceneServerComm.SocketClose(socket);

        socket = ChoiceSceneServerComm.Connect();
        ChoiceSceneServerComm.SendStageChoice(socket, "MediaDetails", formatFileName(Choice));
        ChoiceSceneServerComm.GetDetails(socket);
        ChoiceSceneServerComm.SocketClose(socket);

    }

    public void SetBackgroundImage(String Choice) {
        try {
            File imageFile = new File("BackgroundPictures/" + Choice);
            if (imageFile.exists()) {
                Image img = new Image(imageFile.toURI().toString());
                ChoiceImage.setImage(img);
            } else {
                System.out.println("Image not found: " + imageFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void SaveToLibrary(){
        Image img;
        if (!SavedOrNot) {
            img = new Image(getClass().getResource("/com/app/Application/ChoiceDisplay/starfull.png").toExternalForm());
            SavedOrNot = true;
            AddToLibrary();
        } else {
            img = new Image(getClass().getResource("/com/app/Application/ChoiceDisplay/star.png").toExternalForm());
            SavedOrNot = false;
            RemoveFromLibrary();
        }

        LibraryIcon.setImage(img);
    }

    public void AddToLibrary () {
        Socket socket = LibraryServerComm.Connect();
        LibraryServerComm.AddToLibrary(socket, "Add To library", ServerIdentification.getUserName(), ServerIdentification.getPassword(), MediaIdentification.GetChoice());
        System.out.println(LibraryServerComm.ResultEditLibrary(socket));
        LibraryServerComm.SocketClose(socket);


        socket = LibraryServerComm.Connect();
        LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
        LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
        LibraryServerComm.SocketClose(socket);


    }

    public void RemoveFromLibrary () {
        Socket socket = LibraryServerComm.Connect();
        LibraryServerComm.AddToLibrary(socket, "Remove From library", ServerIdentification.getUserName(), ServerIdentification.getPassword(), MediaIdentification.GetChoice());
        System.out.println(LibraryServerComm.ResultEditLibrary(socket));
        LibraryServerComm.SocketClose(socket);


        socket = LibraryServerComm.Connect();
        LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
        LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
        LibraryServerComm.SocketClose(socket);
    }

    @FXML
    public void initialize()  {
    }

    @FXML private Pane MenuPane;
    @FXML private Pane ProfilePane;
    private boolean VisibleMenu;

    private boolean VisibleProfile;

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

    public void ClickedOnLibrary() {
        MenuAndProfileUtility.switchToLibraryScene(rootPane);
    }

    public void ClickedOnHome() {
        MenuAndProfileUtility.switchToHomeScene(rootPane);
    }

    public void ClickedOnLogOut() {
        MenuAndProfileUtility.switchToLogIn(rootPane);
    }
}
