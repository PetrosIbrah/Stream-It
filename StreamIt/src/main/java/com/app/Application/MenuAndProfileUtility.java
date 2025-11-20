package com.app.Application;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Objects;

public class MenuAndProfileUtility {
    private static final Logger log = LogManager.getLogger(MenuAndProfileUtility.class);

    public static void switchToLibraryScene(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(MenuAndProfileUtility.class.getResource("/com/app/Application/Menu/Library.fxml"))
            );

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    Objects.requireNonNull(MenuAndProfileUtility.class.getResource("/com/app/Application/Home/Home.css")).toExternalForm()
            );

            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            log.error("Inable to swap to Library scene.");
        }
    }

    public static void switchToHomeScene(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(MenuAndProfileUtility.class.getResource("/com/app/Application/Home/HomeScene.fxml"))
            );
            Scene scene = new Scene(root);
            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            log.error("Inable to swap to Home scene.");
        }
    }

    public static void switchToLogIn(AnchorPane rootPane) {
        try {
            Stage oldStage = (Stage) rootPane.getScene().getWindow();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(MenuAndProfileUtility.class.getResource("/com/app/LogIn/LogInScene.fxml")));

            Scene scene = new Scene(root, 600, 367);

            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(false);
            stage.centerOnScreen();

            oldStage.close();

            stage.show();

        } catch (Exception e) {
            log.error("Inable to swap to Log In scene.");
        }
    }

    public static void ShutDownApp(){
        Platform.exit();
    }

}
