package com.app.ProfileControllers;

import com.app.Utility.DefaultServerComm;
import com.app.Utility.ServerCommunication.SettingsServerComm;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;

public class ChangeUsernameController {
    private static final Logger log = LogManager.getLogger(ChangeUsernameController.class);

    @FXML private Pane ErrorPane;
    @FXML private AnchorPane rootPane;
    @FXML private Text InfoText;
    @FXML private TextField EmailFill;
    @FXML private PasswordField PasswordFill;
    @FXML private TextField newUsername;

    @FXML private void initialize()  {
        InfoText.setVisible(false);

        log.info("Change Pass scene initialized Successfully");
    }

    @FXML private void ChangeUserAction() {
        SSLSocket socket = DefaultServerComm.Connect();
        if (socket == null) {
            ErrorPane.setVisible(true);
            return;
        }
        SettingsServerComm.RequestChangeUsername(socket, "Change Username", EmailFill.getText(), PasswordFill.getText(), newUsername.getText());
        log.info(SettingsServerComm.SettingsResult(socket));
        DefaultServerComm.SocketClose(socket);

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML private void CloseErrorPane () {
        ErrorPane.setVisible(false);
    }
}
