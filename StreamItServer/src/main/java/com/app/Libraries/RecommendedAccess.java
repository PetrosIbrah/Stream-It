package com.app.Libraries;

import com.app.Identification.Recommended;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

public class RecommendedAccess {
    public static void SendRecommended (Socket socket) {
        Recommended recommended = JsonWrap();
        SendMoviesToClient(socket, recommended);
    }

    public static void SendMoviesToClient (Socket socket, Recommended recommended){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(recommended.getAllRecommended().length);

            for (String item : recommended.getAllRecommended()) {
                dos.writeUTF(item);
            }

            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Recommended JsonWrap() {
        Recommended wrapper = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Recommended.json");

            wrapper = gson.fromJson(reader, Recommended.class);

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wrapper;
    }
}
