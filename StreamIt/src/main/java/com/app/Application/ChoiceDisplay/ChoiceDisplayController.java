package com.app.Application.ChoiceDisplay;

import com.app.Application.SceneSwapper;
import com.app.Identification.LibraryIdentification;
import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;
import com.app.ServerCommunication.ChoiceSceneServerComm;
import com.app.ServerCommunication.LibraryServerComm;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChoiceDisplayController {
    private static final Logger log = LogManager.getLogger(ChoiceDisplayController.class);

    @FXML private AnchorPane rootPane;
    @FXML private TextFlow textflowDesc;
    @FXML private ImageView ChoiceImage;
    @FXML private Text HomeText;
    @FXML private Text textDesc;
    @FXML private FlowPane SidePane;
    @FXML private HBox pinnedControls;
    @FXML private ScrollPane sideScrollPane;
    @FXML private ImageView LibraryIcon;
    @FXML private Pane MenuPane;
    @FXML private Pane ProfilePane;

    private Boolean SavedOrNot;
    private boolean VisibleMenu;
    private boolean VisibleProfile;

    public void InitializeData(String filename){
        String Choice;
        Choice = filename;
        MediaIdentification.SetChoice(Choice);
        HomeText.setText(formatFileName(filename));
        GetBackground(Choice);
        SetBackgroundImage(Choice);

        String choice = MediaIdentification.GetChoice();
        String[] savedFiles = LibraryIdentification.getSavedFileNames();

        if (Arrays.asList(savedFiles).contains(choice)) {
            Image img = new Image(Objects.requireNonNull(getClass().getResource("/com/app/Application/ChoiceDisplay/starfull.png")).toExternalForm());
            LibraryIcon.setImage(img);
            SavedOrNot = true;
        } else {
            Image img = new Image(Objects.requireNonNull(getClass().getResource("/com/app/Application/ChoiceDisplay/star.png")).toExternalForm());
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

        ChoiceImage.fitWidthProperty().bind(rootPane.widthProperty().subtract(130));
        ChoiceImage.fitHeightProperty().bind(rootPane.heightProperty().subtract(71));

        textDesc.wrappingWidthProperty().bind(textflowDesc.widthProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(textflowDesc.widthProperty());
        clip.heightProperty().bind(textflowDesc.heightProperty());
        textflowDesc.setClip(clip);

        HomeText.wrappingWidthProperty().bind(rootPane.widthProperty().subtract(200));
    }

    private String formatFileName(String fileName) {
        if (fileName == null) return null;

        String withoutExtension = fileName.replaceFirst("(?i)\\.jpg$", "");

        return withoutExtension.replace("_", " ");
    }

    private void GetBackground(String Choice) {
        Socket socket = ChoiceSceneServerComm.Connect();
        ChoiceSceneServerComm.SendStageChoice(socket, "Background", Choice);
        ChoiceSceneServerComm.GetBackgroundImage(socket, Choice);
        ChoiceSceneServerComm.SocketClose(socket);

        socket = ChoiceSceneServerComm.Connect();
        ChoiceSceneServerComm.SendStageChoice(socket, "MediaDetails", formatFileName(Choice));
        ChoiceSceneServerComm.GetDetails(socket);
        ChoiceSceneServerComm.SocketClose(socket);
    }

    private void SetBackgroundImage(String Choice) {
        try {
            File imageFile = new File("BackgroundPictures/" + Choice);
            if (imageFile.exists()) {
                Image img = new Image(imageFile.toURI().toString());
                ChoiceImage.setImage(img);
            } else {
                String msg = "Image not found: " + imageFile.getAbsolutePath();
                log.warn(msg);
            }
        } catch (Exception e) {
            log.error("Unable to Set the background Image");
        }
    }

    @FXML private void SaveToLibrary(){
        Image img;
        if (!SavedOrNot) {
            img = new Image(Objects.requireNonNull(getClass().getResource("/com/app/Application/ChoiceDisplay/starfull.png")).toExternalForm());
            SavedOrNot = true;
            AddToLibrary();
        } else {
            img = new Image(Objects.requireNonNull(getClass().getResource("/com/app/Application/ChoiceDisplay/star.png")).toExternalForm());
            SavedOrNot = false;
            RemoveFromLibrary();
        }

        LibraryIcon.setImage(img);
    }

    private void AddToLibrary () {
        Socket socket = LibraryServerComm.Connect();
        LibraryServerComm.AddToLibrary(socket, "Add To library", ServerIdentification.getUserName(), ServerIdentification.getPassword(), MediaIdentification.GetChoice());
        log.info(LibraryServerComm.ResultEditLibrary(socket));
        LibraryServerComm.SocketClose(socket);

        socket = LibraryServerComm.Connect();
        LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
        LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
        LibraryServerComm.SocketClose(socket);
    }

    private void RemoveFromLibrary () {
        Socket socket = LibraryServerComm.Connect();
        LibraryServerComm.AddToLibrary(socket, "Remove From library", ServerIdentification.getUserName(), ServerIdentification.getPassword(), MediaIdentification.GetChoice());
        log.info(LibraryServerComm.ResultEditLibrary(socket));
        LibraryServerComm.SocketClose(socket);

        socket = LibraryServerComm.Connect();
        LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
        LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
        LibraryServerComm.SocketClose(socket);
    }

    @FXML
    private void initialize()  {
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
        SceneSwapper.switchToLibraryScene(rootPane);
    }

    @FXML private void ClickedOnHome() {
        SceneSwapper.switchToHomeScene(rootPane);
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
}
