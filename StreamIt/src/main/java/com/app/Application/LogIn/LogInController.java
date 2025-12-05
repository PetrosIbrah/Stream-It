package com.app.Application.LogIn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import com.app.Utility.SceneSwapper;
import com.app.Identification.LibraryIdentification;
import com.app.Identification.ServerIdentification;
import com.app.Utility.DefaultServerComm;
import com.app.Utility.ServerCommunication.LibraryServerComm;
import com.app.Utility.ServerCommunication.LogInServerComm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogInController {
    private static final Logger log = LogManager.getLogger(LogInController.class);

    @FXML private Pane ErrorPane;
    @FXML private AnchorPane rootPane;
    @FXML private ImageView LoadingGif;
    @FXML private CheckBox RememberCheckBox;
    @FXML private Text InfoText;
    @FXML private TextField UserFill;
    @FXML private PasswordField PasswordFill;

    @FXML private void initialize()  {
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
            } catch (Exception e) {
                log.error("Failed to read saved login information.");
            }
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    log.error("Thread Couldn't sleep on start up for automatic log in");
                }
                Platform.runLater(this::SwitchToHomePage);
            }).start();
        }
        log.info("Log in scene initialized Successfully");
    }

    @FXML private void LogInAction()  {
        SwitchToHomePage ();

        if (RememberCheckBox.isSelected()) {
            try (FileWriter writer = new FileWriter("rememberme.txt")) {
                writer.write(UserFill.getText() + "\n");
                writer.write(PasswordFill.getText());
            } catch (IOException e) {
                log.warn("Couldn't save the log in info for remember me log in");
            }
        } else {
            DeleteRememberMe();
        }
    }

    @FXML private void SignUpAction() {
        Socket socket = DefaultServerComm.Connect();
        if (socket == null) {
            ErrorPane.setVisible(true);
            return;
        }
        LogInServerComm.SendStageAndCredentials(socket, "Sign Up", UserFill.getText(), PasswordFill.getText());
        String Result = LogInServerComm.GetLogInResult(socket);
        DefaultServerComm.SocketClose(socket);

        if (Result.equals("Account Created")) {
            log.info("Account successfully created");
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

    @FXML private void RememberMeFunction () {
        if (!RememberCheckBox.isSelected()) {
            DeleteRememberMe();
        }
    }

    @FXML private void CloseErrorPane () {
        ErrorPane.setVisible(false);
    }

    private void SwitchToHomePage ()  {
        ServerIdentification.setUserName(UserFill.getText());
        ServerIdentification.setPassword(PasswordFill.getText());
        Socket socket = DefaultServerComm.Connect();
        if (socket == null) {
            ErrorPane.setVisible(true);
            return;
        }
        LogInServerComm.SendStageAndCredentials(socket, "Log In", UserFill.getText(), PasswordFill.getText());
        String Result = LogInServerComm.GetLogInResult(socket);
        DefaultServerComm.SocketClose(socket);

        if (Result.equals("Log In Accepted")) {
            SceneSwapper.switchToSpesificHomeScene(rootPane, "Home");
            log.info("Successfully Logged in");
        } else if (Result.equals("Log In NOT Accepted")) {
            log.warn("No Such Account Exists");
            InfoText.setText("No Such Account Exists");
            InfoText.setVisible(true);
        } else {
            InfoText.setText(Result);
            InfoText.setVisible(true);
        }
        socket = DefaultServerComm.Connect();
        if (socket == null) {
            ErrorPane.setVisible(true);
            return;
        }
        LibraryServerComm.RequestFromLibrary(socket, "Get All From Library", ServerIdentification.getUserName(), ServerIdentification.getPassword());
        LibraryIdentification.setSavedFileNames(LibraryServerComm.GetAllLibraryItems(socket));
        DefaultServerComm.SocketClose(socket);
    }

    private void DeleteRememberMe () {
        File rememberFile = new File("rememberme.txt");
        if (!rememberFile.delete()) {
            log.warn("Couldn't delete remember me file.");
        }
    }
}
