package com.app.Application.VideoPlayer;

import com.app.Application.ChoiceDisplay.ChoiceDisplayController;
import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;
import com.app.ServerCommunication.VideoPlayerServerComm;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;

import java.net.Socket;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class VideoPlayerController {

    private boolean Pause;
    private double Percentage = 0;
    private boolean isMuted = false;

    @FXML private ImageView videoImageView;
    @FXML private ImageView PausePlayIcon;
    @FXML private AnchorPane controlsPane;
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane BackToChoicePane;
    @FXML private ImageView FullScreenImage;
    @FXML private Slider VolumeBar;
    @FXML private ImageView LoadingImage;
    @FXML private CheckBox AdaptiveCheck;


    private final Image PauseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/app/Application/VideoPlayer/pause.png")));
    private final Image PlayImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/app/Application/VideoPlayer/play.png")));

    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer mediaPlayer;
    private volatile boolean playerReady = false;

    private PauseTransition hideControlsTransition;

    @FXML
    public void initialize() {


        AdaptiveCheck.setOnAction(e -> {
            CheckAdaptive();

        });

        VolumeBar.setMin(0);
        VolumeBar.setMax(100);
        VolumeBar.setValue(50);


        hideControlsTransition = new PauseTransition(Duration.seconds(3));
        hideControlsTransition.setOnFinished(e -> {
            BackToChoicePane.setVisible(false);
            controlsPane.setVisible(false);
        });

        videoImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
            }
            videoImageView.fitWidthProperty().bind(newScene.widthProperty());
            videoImageView.fitHeightProperty().bind(newScene.heightProperty());
        });

        startPlayer();


        Platform.runLater(() -> {
            videoImageView.fitWidthProperty().bind(videoImageView.getScene().widthProperty());
            videoImageView.fitHeightProperty().bind(videoImageView.getScene().heightProperty());
        });


        progressBar.setOnMouseClicked(event -> {
            if (mediaPlayer != null && MediaIdentification.GetDuration() > 0) {
                double mouseX = event.getX();
                double width = progressBar.getWidth();
                double percent = mouseX / width;
                long seekTime = (long) (percent * MediaIdentification.GetDuration());
                ClickedOnProgress(seekTime);
                Percentage =  (double) seekTime /MediaIdentification.GetDuration();

            }
        });



        /*
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.setOnCloseRequest(e -> dispose());


            }
        });
        */
    }

    private void ClickedOnProgress (long seekTime) {
        if (mediaPlayer != null) {
            mediaPlayer.controls().setTime(seekTime);
        }
        onTogglePlayPause();
        LoadingImage.setVisible(true);
        Socket socket = VideoPlayerServerComm.Connect();
        VideoPlayerServerComm.SendTimeStamp(socket, "LoadingBar", seekTime);
        //String Assurance = ProgressBarServerComm.GetAssurance(socket);
        VideoPlayerServerComm.SocketClose(socket);
        restartPlayer();

    }

    @FXML
    void onTogglePlayPause() {
        if (!playerReady) return;

        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().pause();
            Pause = true;
            PausePlayIcon.setImage(PlayImage);
            stopProgressUpdater();
        } else {
            mediaPlayer.controls().play();
            Pause = false;
            PausePlayIcon.setImage(PauseImage);
            startProgressUpdater();
        }
    }

    @FXML
    void onStop(ActionEvent evt) {
        if (playerReady) {
            mediaPlayer.controls().stop();
            stopProgressUpdater();
        }
    }

    @FXML
    public void switchToChoiceScene()  {
        try {
            dispose();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/Application/ChoiceDisplay/ChoiceDisplay.fxml"));
            Parent root = loader.load();

            ChoiceDisplayController controller = loader.getController();

            controller.InitializeData(MediaIdentification.GetChoice());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/app/Application/ChoiceDisplay/ChoiceDisplay.css").toExternalForm());

            stage.setTitle("StreamIt");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void toggleFullScreen() {
        Stage stage = (Stage) FullScreenImage.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    public void CheckAdaptive () {
        if (AdaptiveCheck.isSelected()) {
            System.out.println("Adaptive is checked!");
            new Thread(() -> {
                SpeedTest();
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                CheckAdaptive ();
            }).start();
        } else {
            System.out.println("Adaptive is NOT checked!");
        }
    }

    private void onMouseMoved(MouseEvent event) {

        BackToChoicePane.setVisible(true);
        controlsPane.setVisible(true);
        hideControlsTransition.playFromStart();
    }

    public void dispose() {
        if (mediaPlayer != null && mediaPlayer.status().isPlayable()) {
            mediaPlayer.controls().stop();
        }
        if (mediaPlayer != null) mediaPlayer.release();
        if (mediaPlayerFactory != null) mediaPlayerFactory.release();
        stopProgressUpdater();
        AdaptiveCheck.setSelected(false);
    }

    private Timer progressTimer;

    @FXML
    private ProgressBar progressBar;

    private void startProgressUpdater() {
        progressTimer = new Timer(true); // daemon thread
        progressTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.status().isPlaying()) {
                    long currentTime = mediaPlayer.status().time();
                    long totalDuration = MediaIdentification.GetDuration();

                    if (totalDuration > 0) {
                        double progress = (double) currentTime / totalDuration;
                        // double trueprogress = progress + Percentage;
                        double trueprogress = Math.max(0.0, Math.min(1.0, progress + Percentage));
                        Platform.runLater(() -> progressBar.setProgress(trueprogress));
                    }
                }
            }
        }, 0, 200);
    }

    private void stopProgressUpdater() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
    }


    public void restartPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.controls().stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
        if (mediaPlayerFactory != null) {
            mediaPlayerFactory.release();
            mediaPlayerFactory = null;
        }

        startPlayer();
    }


    public void startPlayer() {
        new Thread(() -> {
            mediaPlayerFactory = new MediaPlayerFactory();
            mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

            Platform.runLater(() -> {
                mediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImageView));
                playerReady = true;

                mediaPlayer.media().play("tcp://" + ServerIdentification.GetHost() + ":7778");
                Pause = false;
                PausePlayIcon.setImage(PauseImage);
                startProgressUpdater();



                VolumeBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (!isMuted && mediaPlayer != null) {
                        mediaPlayer.audio().setVolume(newVal.intValue());
                    }
                });

            });
            LoadingImage.setVisible(false);
        }).start();
    }

    public void SpeedTest() {
        CountDownLatch CD = new CountDownLatch(1);
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                double Speed = report.getTransferRateBit().doubleValue() / 1000000;
                String msg = "Successful completion of speedtest " + "Net Speed: " + String.format("%.2f", Speed) + "Mbs";
                System.out.println(msg);
                CD.countDown();

                Socket socket = VideoPlayerServerComm.Connect();
                long Time = (long) (progressBar.getProgress() * MediaIdentification.GetDuration());
                VideoPlayerServerComm.SendAdaptive(socket,"Adaptive", Time, Speed);
                String Restart = VideoPlayerServerComm.ReceiveRestart(socket);
                VideoPlayerServerComm.SocketClose(socket);

                if (Restart.equals("Restart")){
                    onTogglePlayPause();
                    Percentage =  (double) progressBar.getProgress();
                    LoadingImage.setVisible(true);
                    restartPlayer();
                }
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                System.out.println("Couldn't complete speedtest");
                CD.countDown();
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {}
        });

        String downloadUrl = "http://speedtest.tele2.net/1MB.zip";
        speedTestSocket.startFixedDownload(downloadUrl, 5000, 1000);

        try{
            CD.await();
        } catch (InterruptedException e) {
            System.out.println("Couldn't await for speedtest");
        }
    }
}