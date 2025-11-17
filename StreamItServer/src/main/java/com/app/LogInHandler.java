package com.app;

import com.app.Repositories.AccountsRepo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LogInHandler {

    public static void LogIn (Socket socket, String Username, String Password){
        SendLogInResult(socket, AccountsRepo.CheckLogIn(Username, Password));
    }

    public static void SignUp (Socket socket, String Username, String Password){
        SendLogInResult(socket, AccountsRepo.SaveAccount(Username, Password));
    }

    public static void SendLogInResult(Socket socket, String Restart){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Restart);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
