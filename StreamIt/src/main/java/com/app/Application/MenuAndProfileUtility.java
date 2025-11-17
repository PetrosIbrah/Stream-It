package com.app.Application;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuAndProfileUtility {
    public static void switchToLibraryScene(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    MenuAndProfileUtility.class.getResource("/com/app/Application/Menu/Library.fxml")
            );

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    MenuAndProfileUtility.class.getResource("/com/app/Application/Home/Home.css").toExternalForm()
            );

            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchToHomeScene(AnchorPane rootPane) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    MenuAndProfileUtility.class.getResource("/com/app/Application/Home/HomeScene.fxml")
            );

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    MenuAndProfileUtility.class.getResource("/com/app/Application/Home/Home.css").toExternalForm()
            );

            stage.setTitle("StreamIt");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchToLogIn(AnchorPane rootPane) {
        try {
            Stage oldStage = (Stage) rootPane.getScene().getWindow();
            Stage stage = new Stage();

            Parent root = FXMLLoader.load(MenuAndProfileUtility.class.getResource("/com/app/LogIn/LogInScene.fxml"));

            Scene scene = new Scene(root, 600, 367);
            scene.getStylesheets().add(MenuAndProfileUtility.class.getResource("/com/app/LogIn/LogIn.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(false);
            stage.centerOnScreen();

            oldStage.close();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ShutDownApp(){
        Platform.exit();
    }

}
