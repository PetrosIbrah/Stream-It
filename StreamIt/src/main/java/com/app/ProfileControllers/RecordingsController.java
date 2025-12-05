package com.app.ProfileControllers;

import com.app.Utility.CallableFunctions;
import com.app.Utility.SceneSwapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;

public class RecordingsController {
    private static final Logger log = LogManager.getLogger(RecordingsController.class);

    @FXML private AnchorPane rootPane;
    @FXML private Pane MenuPane;
    @FXML private Pane ProfilePane;
    @FXML private Text HomeText;
    @FXML private ChoiceBox<String> AudioChoicebox;
    @FXML private TextField PathText;
    @FXML private Button chooseButton;

    private boolean VisibleMenu;
    private boolean VisibleProfile;

    @FXML private void initialize(){
        VisibleProfile = false;
        VisibleMenu = false;

        AudioChoicebox.setItems(FXCollections.observableArrayList(CallableFunctions.GetAllAudioDevices()));

        AudioChoicebox.setValue(CallableFunctions.loadAudioDevice());

        PathText.setText(CallableFunctions.loadRecordingsPath());

        HomeText.wrappingWidthProperty().bind(rootPane.widthProperty().subtract(200));
    }

    @FXML private void onChooseFolder () {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder");

        Stage stage = (Stage) chooseButton.getScene().getWindow();
        File selected = chooser.showDialog(stage);

        if (selected != null) {
            log.info("Selected path: {}", selected.getAbsolutePath());
            CallableFunctions.saveRecordingsPath(selected.getAbsolutePath());
            PathText.setText(CallableFunctions.loadRecordingsPath());
        }
    }

    @FXML private void onAudioSelected(){
        String selected = AudioChoicebox.getValue();
        CallableFunctions.saveAudioDevice(selected);
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
        SceneSwapper.switchToSpesificHomeScene(rootPane,"Library");
    }

    @FXML private void ClickedOnRecommended() {
        SceneSwapper.switchToSpesificHomeScene(rootPane,"Recommended");
    }

    @FXML private void ClickedOnMovies() {
        SceneSwapper.switchToSpesificHomeScene(rootPane,"Movies");
    }

    @FXML private void ClickedOnShows() {
        SceneSwapper.switchToSpesificHomeScene(rootPane,"Shows");
    }

    @FXML private void ClickedOnHome() {
        SceneSwapper.switchToSpesificHomeScene(rootPane, "Home");
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
}
