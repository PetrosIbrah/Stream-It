package com.app.Utility;

import com.app.Application.ChoiceDisplay.ChoiceDisplayController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Objects;

public class SceneSwapper {
    private static final Logger log = LogManager.getLogger(SceneSwapper.class);

    public static void switchToLibraryScene(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            SceneSwapper.class.getResource("/com/app/Application/Menu/Library.fxml")
                    )
            );

            Scene scene = new Scene(root);
            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            log.info("Successfully initiated Library scene");
        } catch (Exception e) {
            log.error("Unable to swap to Library scene.");
        }
    }

    public static void switchToHomeScene(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(SceneSwapper.class.getResource("/com/app/Application/Home/HomeScene.fxml"))
            );
            Scene scene = new Scene(root);
            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            log.info("Successfully initiated Home scene");
        } catch (Exception e) {
            log.error("Unable to swap to Home scene.");
        }
    }

    public static void switchToLogIn(AnchorPane rootPane) {
        try {
            Stage oldStage = (Stage) rootPane.getScene().getWindow();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneSwapper.class.getResource("/com/app/Application/LogIn/LogInScene.fxml")));

            Scene scene = new Scene(root, 600, 367);

            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(false);
            stage.centerOnScreen();

            oldStage.close();

            stage.show();
            log.info("Successfully initiated Log in scene");
        } catch (Exception e) {
            log.error("Unable to swap to Log In scene.");
        }
    }

    public static void switchToChoiceDisplay(AnchorPane rootPane, String Filename) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(SceneSwapper.class.getResource("/com/app/Application/ChoiceDisplay/ChoiceDisplay.fxml"));
            Parent root = loader.load();

            ChoiceDisplayController controller = loader.getController();

            controller.InitializeData(Filename);

            Scene scene = new Scene(root, 1280, 720);

            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setMinWidth(854);
            stage.setMinHeight(480);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            log.info("Successfully initiated Choice scene");
        } catch (Exception e) {
            log.error("Unable to swap to Choice scene.");
        }
    }

    public static void ShutDownApp(){
        Platform.exit();
    }

}
