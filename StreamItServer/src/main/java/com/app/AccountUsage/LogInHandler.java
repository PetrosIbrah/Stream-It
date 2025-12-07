package com.app.AccountUsage;

import com.app.AccountUsage.LogInSignUp.AccountsRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.PrintWriter;

public class LogInHandler {
    private static final Logger log = LogManager.getLogger(LogInHandler.class);

    public static void LogIn (SSLSocket socket, String Username, String Password){
        SendLogInResult(socket, AccountsRepo.CheckLogIn(Username, Password));
    }

    public static void SignUp (SSLSocket socket, String Username, String Password){
        SendLogInResult(socket, AccountsRepo.SaveAccount(Username, Password));
    }

    public static void SendLogInResult(SSLSocket socket, String Restart){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Restart);
        } catch (IOException e) {
            log.error("Unable to send Log in result properly.");
        }
    }
}