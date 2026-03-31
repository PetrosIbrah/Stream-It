package com.app.Application.LogIn;

import com.app.Utility.DefaultServerComm;
import com.app.Utility.SceneSwapper;
import com.app.Utility.ServerCommunication.LogInServerComm;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocket;

public class SignUpController {
    private static final Logger log = LogManager.getLogger(SignUpController.class);

    @FXML private Pane ErrorPane;
    @FXML private AnchorPane rootPane;
    @FXML private Text InfoText;
    @FXML private TextField UserFill;
    @FXML private TextField EmailFill;
    @FXML private PasswordField PasswordFill;

    @FXML private void initialize()  {
        InfoText.setVisible(false);

        log.info("Log in scene initialized Successfully");
    }

    @FXML private void LogInAction()  {
        SceneSwapper.switchToLogIn(rootPane);
    }

    @FXML private void SignUpAction() {
        SSLSocket socket = DefaultServerComm.Connect();
        if (socket == null) {
            ErrorPane.setVisible(true);
            return;
        }
        LogInServerComm.SendSignUpAndCredentials(socket, "Sign Up", UserFill.getText(), EmailFill.getText(), PasswordFill.getText());
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


    @FXML private void CloseErrorPane () {
        ErrorPane.setVisible(false);
    }
}
