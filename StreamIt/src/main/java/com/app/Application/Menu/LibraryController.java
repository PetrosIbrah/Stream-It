package com.app.Application.Menu;

import com.app.Application.MenuAndProfileUtility;
import com.app.Identification.LibraryIdentification;
import com.app.Identification.ServerIdentification;
import com.app.ServerCommunication.LibraryServerComm;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.net.Socket;
import java.util.Arrays;

public class LibraryController {
    private static final Logger log = LogManager.getLogger(LibraryController.class);

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
        Socket socket = LibraryServerComm.Connect();
        LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
        LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
        DisplayImages(LibraryIdentification.getSavedFileNames(), GalleryFlowPane);
        log.info(Arrays.toString(LibraryIdentification.getSavedFileNames()));
        LibraryServerComm.SocketClose(socket);

        HomeText.wrappingWidthProperty().bind(rootPane.widthProperty().subtract(200));
    }

    private void DisplayImages(String[] imageFiles, FlowPane container) {
        container.getChildren().clear();

        for (String fileName : imageFiles) {
            File dir = new File("ReferencePictures");
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    log.error("Failed to create directory `ReferencePictures`");
                }
            }

            File imageFile = new File(dir, fileName);

            if (!imageFile.exists()) continue;

            Image img = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(img);

            imageView.setFitWidth(155);
            imageView.setFitHeight(211);
            imageView.setPreserveRatio(true);

            Button invisibleButton = new Button();
            invisibleButton.setOpacity(0);
            invisibleButton.setPrefSize(155, 211);
            invisibleButton.setOnAction(e -> MenuAndProfileUtility.switchToChoiceDisplay(rootPane, fileName));

            StackPane stack = new StackPane(imageView, invisibleButton);

            container.getChildren().add(stack);
        }

        container.setHgap(25);
        container.setVgap(50);
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
        File rememberFile = new File("rememberme.txt");
        if (!rememberFile.delete()) {
            log.warn("Couldn't delete remember me file.");
        }
        MenuAndProfileUtility.switchToLogIn(rootPane);
    }

    @FXML private void ClickedOnExit() {
        MenuAndProfileUtility.ShutDownApp();
    }
}
