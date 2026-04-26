package com.app;

import com.app.Identification.FileIdentification;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.Comparator;
import java.nio.file.*;
import java.util.Objects;
import java.util.stream.Stream;

public class Main extends Application {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("StreamIt.png")).toExternalForm());
            stage.getIcons().add(image);

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Application/LogIn/LogInScene.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("StreamIt");
            stage.setResizable(false);
            stage.show();

            log.info("Log in scene initiated successfully");
        } catch(Exception e) {
            log.fatal("Failed to initiate the Log in scene");
        }
    }

    @Override
    public void stop() {
        DeleteDirectory(FileIdentification.ReferencePictures);
        DeleteDirectory(FileIdentification.BackgroundPictures);

        Platform.exit();
    }

    private static void DeleteDirectory(String Path) {
        try {
            try (Stream<Path> paths = Files.walk(Paths.get(Path))) {
                paths.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                log.warn("Failed to delete{}", Path);
                            }
                        });
            }
        } catch (IOException e) {
            log.warn("Failed To delete Directory While shutting down");
        }
    }
}