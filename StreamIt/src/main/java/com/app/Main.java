package com.app;

import atlantafx.base.theme.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;

import java.io.IOException;
import java.nio.file.*;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("LogIn/LogInScene.fxml"));
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("LogIn/LogIn.css").toExternalForm());

            // Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(false);

            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        try {
            // String dirToDelete = "ReferencePictures";
            deleteDirectory("ReferencePictures");
            deleteDirectory("BackgroundPictures");
            // System.out.println("Deleted directory: " + dirToDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(String Path) throws IOException {
        Files.walk(Paths.get(Path))
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void main(String[] args) {
        launch(args);
    }
}