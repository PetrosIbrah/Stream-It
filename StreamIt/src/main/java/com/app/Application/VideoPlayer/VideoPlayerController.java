package com.app.Application.VideoPlayer;

import com.app.Application.ChoiceDisplay.SidePaneHandler;
import com.app.Utility.CallableFunctions;
import com.app.Utility.SceneSwapper;
import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;
import com.app.Utility.DefaultServerComm;
import com.app.Utility.ServerCommunication.VideoPlayerServerComm;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.controlsfx.control.ToggleSwitch;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

public class VideoPlayerController {
    private static final Logger log = LogManager.getLogger(VideoPlayerController.class);

    private final Image PauseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/app/Application/VideoPlayer/pause.png")));
    private final Image PlayImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/app/Application/VideoPlayer/play.png")));
    private final Image Record = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/app/Icons/Record.png")));
    private final Image StopRecording = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/app/Icons/StopRecord.png")));

    @FXML private Pane ErrorPane;
    @FXML private ImageView videoImageView;
    @FXML private ImageView PausePlayIcon;
    @FXML private AnchorPane controlsPane;
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane BackToChoicePane;
    @FXML private ImageView FullScreenImage;
    @FXML private Slider VolumeBar;
    @FXML private ImageView LoadingImage;
    @FXML private ToggleSwitch AdaptiveCheck;
    @FXML private ProgressBar progressBar;
    @FXML private Text RecInfo;
    @FXML private ImageView RecImage;

    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer mediaPlayer;
    private volatile boolean playerReady = false;
    private boolean Playing;
    private double Percentage = 0;
    private final boolean isMuted = false;
    private PauseTransition hideControlsTransition;
    private Timer progressTimer;
    private boolean Recording;
    private final Recorder recorder = new Recorder();

    @FXML private void initialize() {
        if (Playing){
            log.info("Delete this function in the future if ");
        }
        AdaptiveCheck.selectedProperty().addListener((obs, oldVal, newVal) -> CheckAdaptive());
        Boolean AutoAdapt = CallableFunctions.loadAutoAdapt();
        AdaptiveCheck.setSelected(false);
        if (Boolean.TRUE.equals(AutoAdapt)){
            AdaptiveCheck.setSelected(true);
        }

        VolumeBar.setMin(0);
        VolumeBar.setMax(100);
        VolumeBar.setValue(50);
        PausePlayIcon.setImage(PauseImage);

        hideControlsTransition = new PauseTransition(Duration.seconds(3));
        hideControlsTransition.setOnFinished(e -> {
            BackToChoicePane.setVisible(false);
            controlsPane.setVisible(false);
        });

        videoImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
            }
            videoImageView.fitWidthProperty().bind(Objects.requireNonNull(newScene).widthProperty());
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

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnCloseRequest(e -> {
                            log.info("Application closing");
                            dispose();
                        });
                    }

                    if (newWindow == null && oldWindow != null) {
                        log.info("Swaping scenes");
                        dispose();
                    }
                });
            }
        });
    }

    private void ClickedOnProgress (long seekTime) {
        SSLSocket socket = DefaultServerComm.Connect();
        if (socket == null) {
            ErrorPane.setVisible(true);
            return;
        }
        VideoPlayerServerComm.SendTimeStamp(socket, "LoadingBar", seekTime);
        DefaultServerComm.SocketClose(socket);
        if (mediaPlayer != null) {
            mediaPlayer.controls().setTime(seekTime);
        }
        restartPlayer();
    }

    @FXML private void onTogglePlayPause() {
        if (!playerReady) return;

        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().pause();
            Playing = false;
            PausePlayIcon.setImage(PlayImage);
            stopProgressUpdater();
        } else {
            mediaPlayer.controls().play();
            Playing = true;
            PausePlayIcon.setImage(PauseImage);
            startProgressUpdater();
        }
    }

    @FXML private void switchToChoiceScene()  {
        SceneSwapper.switchToChoiceDisplay(rootPane, MediaIdentification.GetChoice());
    }

    @FXML private void toggleFullScreen() {
        Stage stage = (Stage) FullScreenImage.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML private void ClickedOnRecord()  {
        if (Recording) {
            Recording = false;
            RecInfo.setText("Record Saved");
            RecImage.setImage(Record);
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.warn("Failed to await for 5 seconds to remove record text");
                }
                RecInfo.setText("");
            }).start();
            log.info("Stopped Recording");
            recorder.stopRecording();
        } else {
            Recording = true;
            RecInfo.setText("Recording...");
            RecImage.setImage(StopRecording);
            log.info("Now recording");
            String timestamp = new java.text.SimpleDateFormat("dd-MM-yyyy_HH-mm-ss")
                    .format(new java.util.Date());
            String FinalFile = CallableFunctions.loadRecordingsPath() + "/" + timestamp + ".mp4";

            try {
                recorder.startRecording(
                        "StreamIt",
                        CallableFunctions.loadAudioDevice(),
                        FinalFile
                );
            } catch (Exception e) {
                log.error(" ");
            }
        }
    }

    private void onCloserecording() {
        if (Recording) {
            recorder.stopRecording();
        }
    }

    private void CheckAdaptive () {
        if (AdaptiveCheck.isSelected()) {
            log.info("Adaptive is checked!");
            new Thread(() -> {
                SpeedTest();
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    log.error("Adaptive thread interrupted");
                }
                CheckAdaptive ();
            }).start();
        } else {
            log.info("Adaptive is NOT checked!");
        }
    }

    private void onMouseMoved(MouseEvent event) {
        BackToChoicePane.setVisible(true);
        controlsPane.setVisible(true);
        hideControlsTransition.playFromStart();
    }

    private void dispose() {
        onCloserecording();
        AdaptiveCheck.setSelected(false);
        stopProgressUpdater();

        try {
            if (mediaPlayer != null) {
                mediaPlayer.controls().stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            log.error("Unable to Stop mediaplayer");
        }

        try {
            if (mediaPlayerFactory != null) {
                mediaPlayerFactory.release();
                mediaPlayerFactory = null;
            }
        } catch (Exception e) {
            log.error("Unable to release Media player Factory");
        }

        log.info("VideoPlayerController disposed.");
    }

    private void startProgressUpdater() {
        progressTimer = new Timer(true);
        progressTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.status().isPlaying()) {
                    long currentTime = mediaPlayer.status().time();
                    long totalDuration = MediaIdentification.GetDuration();
                    if (totalDuration > 0) {
                        double progress = (double) currentTime / totalDuration;
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

    private void restartPlayer() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {

                mediaPlayer.controls().stop();
                mediaPlayer.media().play("tcp://" + ServerIdentification.GetHost() + ":7778");

                PausePlayIcon.setImage(PauseImage);
                startProgressUpdater();
                LoadingImage.setVisible(false);
            }
        });
    }

    private void startPlayer() {
        new Thread(() -> {
            mediaPlayerFactory = new MediaPlayerFactory();
            mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

            Platform.runLater(() -> {
                mediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImageView));
                playerReady = true;

                mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                    @Override
                    public void finished(MediaPlayer mediaPlayer) {
                        Platform.runLater(() -> {
                            if(Boolean.TRUE.equals(CallableFunctions.loadNextEp())) {
                                if(nextEpisode(MediaIdentification.getStreamableFile()) != null) {
                                    if(SidePaneHandler.OnVideoRestarted(nextEpisode(MediaIdentification.getStreamableFile()))){
                                    try {
                                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/app/Application/VideoPlayer/VideoPlayer.fxml")));
                                        Stage stage = (Stage) rootPane.getScene().getWindow();
                                        stage.setScene(new Scene(root));
                                    } catch (IOException e) {
                                        log.error("Couln't reload Scene");
                                    }
                                    }
                                }
                            }
                            log.info("Media Completed successfully.");
                        });
                    }
                });

                mediaPlayer.media().play("tcp://" + ServerIdentification.GetHost() + ":7778");
                Playing = true;
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

    private void SpeedTest() {
        CountDownLatch CD = new CountDownLatch(1);
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                double Speed = report.getTransferRateBit().doubleValue() / 1000000;
                String msg = "Successful completion of speedtest " + "Net Speed: " + String.format("%.2f", Speed) + "Mbs";
                log.info(msg);
                CD.countDown();

                SSLSocket socket = DefaultServerComm.Connect();
                if (socket == null) {
                    ErrorPane.setVisible(true);
                    return;
                }
                long Time = (long) (progressBar.getProgress() * MediaIdentification.GetDuration());
                VideoPlayerServerComm.SendAdaptive(socket,"Adaptive", Time, Speed);
                String Restart = VideoPlayerServerComm.ReceiveRestart(socket);
                DefaultServerComm.SocketClose(socket);

                if (Restart.equals("Restart")){
                    Percentage =  progressBar.getProgress();
                    LoadingImage.setVisible(true);
                    restartPlayer();
                }
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                log.error("Couldn't complete speedtest");
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
            log.warn("Couldn't await for speedtest");
        }
    }

    private static String nextEpisode(String filename) {
        if (!filename.contains("Ep")) return null;

        java.util.regex.Pattern episodeFolderPattern = java.util.regex.Pattern.compile("Episode (\\d+)/");
        java.util.regex.Matcher folderMatcher = episodeFolderPattern.matcher(filename);
        if (!folderMatcher.find()) return null;
        int folderEp = Integer.parseInt(folderMatcher.group(1));
        filename = filename.replaceAll("Episode \\d+/", "Episode " + (folderEp + 1) + "/");

        java.util.regex.Pattern filePattern = java.util.regex.Pattern.compile("Ep(\\d+)");
        java.util.regex.Matcher fileMatcher = filePattern.matcher(filename);
        if (!fileMatcher.find()) return null;
        int fileEp = Integer.parseInt(fileMatcher.group(1));
        filename = filename.replaceAll("Ep\\d+", "Ep" + (fileEp + 1));

        return filename;
    }

    @FXML private void CloseErrorPane () {
        ErrorPane.setVisible(false);
    }
}