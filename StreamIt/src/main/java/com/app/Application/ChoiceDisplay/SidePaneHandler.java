package com.app.Application.ChoiceDisplay;

import com.app.Identification.MediaIdentification;
import com.app.ServerCommunication.ChoiceSceneServerComm;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SidePaneHandler {
    public static Socket socket;

    public static void SetUpChoices(FlowPane SidePane, HBox pinnedControls) {
        ChoiceBox<String> seasonChoiceBox = new ChoiceBox<>();
        for (int i = 0; i < MediaIdentification.getSeasons().size(); i++) {
            seasonChoiceBox.getItems().add("Season " + (i + 1));
        }
        seasonChoiceBox.setPrefWidth(150);

        ChoiceBox<String> episodeChoiceBox = new ChoiceBox<>();
        episodeChoiceBox.setPrefWidth(150);

        seasonChoiceBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                int seasonIndex = newVal.intValue();
                int episodeCount = MediaIdentification.getSeasons().get(seasonIndex);

                episodeChoiceBox.getItems().clear();
                for (int ep = 1; ep <= episodeCount; ep++) {
                    episodeChoiceBox.getItems().add("Episode " + ep);
                }
                if (!episodeChoiceBox.getItems().isEmpty()) {
                    episodeChoiceBox.getSelectionModel().selectFirst();
                }
            }
        });

        episodeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                Platform.runLater(() -> {
                    int seasonIndex = seasonChoiceBox.getSelectionModel().getSelectedIndex();
                    int episodeIndex = episodeChoiceBox.getSelectionModel().getSelectedIndex();
                    if (seasonIndex >= 0 && episodeIndex >= 0) {
                        OnChoiceSelected(seasonIndex, episodeIndex, SidePane);
                    }
                });
            }
        });

        pinnedControls.setSpacing(15);
        pinnedControls.setPadding(new Insets(10, 10, 10, 10));
        pinnedControls.getChildren().addAll(seasonChoiceBox, episodeChoiceBox);

        seasonChoiceBox.getStyleClass().add("choice-box");
        episodeChoiceBox.getStyleClass().add("choice-box");


        if (!seasonChoiceBox.getItems().isEmpty()) {
            seasonChoiceBox.getSelectionModel().selectFirst();
        }

    }


    public static void OnChoiceSelected (int seasonIndex, int episodeIndex, FlowPane SidePane) {

        String season = "Season " + (seasonIndex + 1);
        String episode = "Episode " + (episodeIndex + 1);

        System.out.println("User chose: " + season + " - " + episode);
        socket = ChoiceSceneServerComm.Connect();

        ChoiceSceneServerComm.SendStreamChoice(socket, "Videos", MediaIdentification.getTitle() + "/" + season + "/" + episode);
        List<String> videolist = ChoiceSceneServerComm.ReceiveVideoList(socket);

        for (String video : videolist) {
            System.out.println(video);
        }

        SidePane.getChildren().removeIf(node -> !(node instanceof ChoiceBox));

        SetUpButtons(videolist, SidePane);




    }

    public static void SetUpButtons (List<String> videolist, FlowPane SidePane) {
        Pattern resolutionPattern = Pattern.compile("(\\d+)p");
        Map<Integer, List<String>> groupedVideos = new TreeMap<>(Collections.reverseOrder());

        for (String fullPath : videolist) {
            Matcher m = resolutionPattern.matcher(fullPath);
            int res = 0;
            if (m.find()) {
                res = Integer.parseInt(m.group(1));
            }

            groupedVideos.computeIfAbsent(res, k -> new ArrayList<>()).add(fullPath);
        }

        for (Map.Entry<Integer, List<String>> entry : groupedVideos.entrySet()) {
            int resolution = entry.getKey();

            Text heading = new Text(resolution + "p");
            heading.setStyle("-fx-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");
            SidePane.getChildren().add(heading);

            for (String fullPath : entry.getValue()) {
                String fileName = new File(fullPath).getName();
                Button videoButton = new Button(fileName);
                videoButton.setPrefWidth(300);

                videoButton.setOnAction(e -> {
                            OnVideoClicked(fullPath, e);

                            try {

                                Parent root = FXMLLoader.load(Objects.requireNonNull(SidePaneHandler.class.getResource("/com/app/Application/VideoPlayer/VideoPlayer.fxml")));
                                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

                                Scene scene = new Scene(root);


                                // scene.getStylesheets().add(getClass().getResource("/com/app/Application/Home/Home.css").toExternalForm());
                                // Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
                                stage.setTitle("StreamIt");
                                stage.setResizable(true);
                                stage.setScene(scene);
                                stage.centerOnScreen();
                                stage.show();




                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


                        }
                );

                SidePane.getChildren().add(videoButton);
            }
        }
    }

    public static void OnVideoClicked (String fullPath, Event e) {
        Socket socket = ChoiceSceneServerComm.Connect();
        ChoiceSceneServerComm.SendStreamChoice(socket, "StartStream", fullPath);
        // Debuging
        // StreamVideo(socket);
        ChoiceSceneServerComm.SocketClose(socket);
        System.out.println(fullPath);
    }

    public static void StreamVideo(Socket socket){
        try {
            String ffplay = System.getenv("ffplay");
            if (ffplay == null || ffplay.isEmpty()) {
                ffplay = "ffplay";
            }
            String ServerIP = socket.getInetAddress().getHostAddress();

            ProcessBuilder Command = new ProcessBuilder(
                    ffplay,
                    "-window_title", "TCP",
                    "tcp://localhost:4444"
            );

            Command.inheritIO();
            Command.redirectErrorStream(true);
            Command.start();

            System.out.println("Successful TCP Streaming | Client-side ");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Unsuccessful TCP Streaming | Client-side ");
        }
    }
}