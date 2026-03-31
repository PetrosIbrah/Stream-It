package com.app.Utility;

import com.app.Application.ChoiceDisplay.ChoiceDisplayController;
import com.app.Application.Home.HomeSceneController;
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

    public static void switchToSpesificHomeScene(AnchorPane rootPane, String Scenename) {
        String  msg = "Successfully initiated " + Scenename + " scene";
        String  errmsg = "Unable to swap to " + Scenename + " scene.";

        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setMinWidth(0);
            stage.setMinHeight(0);
            stage.setMaxWidth(Double.MAX_VALUE);
            stage.setMaxHeight(Double.MAX_VALUE);

            FXMLLoader loader = new FXMLLoader(SceneSwapper.class.getResource("/com/app/Application/Home/HomeScene.fxml"));
            Parent root = loader.load();

            HomeSceneController controller = loader.getController();

            controller.InitiazeStartingSceneWith(Scenename);

            Scene scene = new Scene(root, 1280, 720);

            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setMinWidth(854);
            stage.setMinHeight(480);
            stage.centerOnScreen();
            stage.show();


            log.info(msg);
        } catch (Exception e) {
            log.error(errmsg);
        }
    }

    public static void switchToLogIn(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneSwapper.class.getResource("/com/app/Application/LogIn/LogInScene.fxml")));

            stage.close();

            Scene scene = new Scene(root, 600, 385);

            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(false);
            stage.setMinWidth(600);
            stage.setMinHeight(385);
            stage.setMaxWidth(600);
            stage.setMaxHeight(385);
            stage.setWidth(600);
            stage.setHeight(385);
            stage.centerOnScreen();
            stage.show();

            log.info("Successfully initiated Log in scene");
        } catch (Exception e) {
            log.error("Unable to swap to Log In scene.");
        }
    }

    public static void switchToSignUp(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneSwapper.class.getResource("/com/app/Application/SignUp/SignUpScene.fxml")));

            stage.close();
            Scene scene = new Scene(root, 600, 385);

            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(false);
            stage.setMinWidth(600);
            stage.setMinHeight(385);
            stage.setMaxWidth(600);
            stage.setMaxHeight(385);
            stage.setWidth(600);
            stage.setHeight(385);
            stage.centerOnScreen();
            stage.show();

            log.info("Successfully initiated Sign Up scene");
        } catch (Exception e) {
            log.error("Unable to swap to Sign Up scene.");
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

    public static void switchToSettings(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(SceneSwapper.class.getResource("/com/app/Profile/Settings.fxml"))
            );
            Scene scene = new Scene(root);
            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            log.info("Successfully initiated Recordings scene");
        } catch (Exception e) {
            log.error("Unable to swap to Recordings scene.");
        }
    }

    public static void ShutDownApp(){
        Platform.exit();
    }

}
