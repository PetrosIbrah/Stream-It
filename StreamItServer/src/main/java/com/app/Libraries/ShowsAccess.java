package com.app.Libraries;

import com.app.Identification.Shows;
import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

public class ShowsAccess {
    public static void SendShows (Socket socket) {
        Shows shows = JsonWrap();
        SendMoviesToClient(socket, shows);
    }

    public static void SendMoviesToClient (Socket socket, Shows shows){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(shows.getAllShows().length);

            for (String item : shows.getAllShows()) {
                dos.writeUTF(item);
            }

            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Shows JsonWrap() {
        Shows wrapper = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Shows.json");

            wrapper = gson.fromJson(reader, Shows.class);

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wrapper;
    }
}
