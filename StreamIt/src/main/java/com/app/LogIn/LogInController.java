package com.app.LogIn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import com.app.Identification.LibraryIdentification;
import com.app.Identification.MediaIdentification;
import com.app.Identification.ServerIdentification;
import com.app.ServerCommunication.HomePageServerComm;
import com.app.ServerCommunication.LibraryServerComm;
import com.app.ServerCommunication.LogInServerComm;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LogInController {

    private Stage stage;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView LoadingGif;

    @FXML
    private CheckBox RememberCheckBox;
    @FXML
    private Text InfoText;

    @FXML
    private TextField UserFill;

    @FXML
    private PasswordField PasswordFill;

    @FXML
    public void initialize()  {
        LoadingGif.setVisible(false);
        InfoText.setVisible(false);

        File file = new File("rememberme.txt");
        if (file.exists()) {
            LoadingGif.setVisible(true);
            RememberCheckBox.setSelected(true);
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    UserFill.setText(scanner.nextLine());
                    PasswordFill.setText(scanner.nextLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    Platform.runLater(() -> {
                        SwitchToHomePage();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    public void SwitchToHomePage ()  {
        ServerIdentification.setUserName(UserFill.getText());
        ServerIdentification.setPassword(PasswordFill.getText());
        Socket socket = LogInServerComm.Connect();
        LogInServerComm.SendStageAndCredentials(socket, "Log In", UserFill.getText(), PasswordFill.getText());
        String Result = LogInServerComm.GetLogInResult(socket);
        LogInServerComm.SocketClose(socket);

        if (Result.equals("Log In Accepted")) {
            try {
                Stage stage = (Stage) rootPane.getScene().getWindow();

                Parent root = FXMLLoader.load(getClass().getResource("/com/app/Application/Home/HomeScene.fxml"));
                Scene scene = new Scene(root, 1280, 720);
                scene.getStylesheets().add(getClass().getResource("/com/app/Application/Home/Home.css").toExternalForm());

                stage.setTitle("StreamIt");
                stage.setScene(scene);
                stage.setResizable(true);
                stage.setMinWidth(854);
                stage.setMinHeight(480);
                stage.centerOnScreen();
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Result.equals("Log In NOT Accepted")) {
            InfoText.setText("No Such Account Exists");
            InfoText.setVisible(true);
        } else {
            InfoText.setText(Result);
            InfoText.setVisible(true);
        }
        socket = LibraryServerComm.Connect();
        LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
        LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
        LibraryServerComm.SocketClose(socket);
    }

    public void LogInAction(ActionEvent event) throws IOException {
        SwitchToHomePage ();

        if (RememberCheckBox.isSelected()) {
            try (FileWriter writer = new FileWriter("rememberme.txt")) {
                writer.write(UserFill.getText() + "\n");
                writer.write(PasswordFill.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new File("rememberme.txt").delete();
        }
    }

    public void SignUpAction(ActionEvent event) throws IOException {
        Socket socket = LogInServerComm.Connect();
        LogInServerComm.SendStageAndCredentials(socket, "Sign Up", UserFill.getText(), PasswordFill.getText());
        String Result = LogInServerComm.GetLogInResult(socket);
        LogInServerComm.SocketClose(socket);

        if (Result.equals("Account Created")) {
            InfoText.setText(Result);
            InfoText.setVisible(true);
            InfoText.setStyle("-fx-fill: green;");
        } else if (Result.equals("Username already exists")) {
            InfoText.setText(Result);
            InfoText.setVisible(true);
        } else {
            InfoText.setText(Result);
            InfoText.setVisible(true);
        }
    }

    public void RememberMeFunction () {
        if (!RememberCheckBox.isSelected()) {
            new File("rememberme.txt").delete();
        }
    }

}
