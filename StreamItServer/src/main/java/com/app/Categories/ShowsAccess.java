package com.app.Categories;

import com.app.Identification.Shows;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class ShowsAccess {
    private static final Logger log = LogManager.getLogger(ShowsAccess.class);

    public static void SendShows (SSLSocket socket) {
        Shows shows = JsonWrap();
        SendMoviesToClient(socket, shows);
    }

    private static void SendMoviesToClient (SSLSocket socket, Shows shows){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(shows.getAllShows().length);

            for (String item : shows.getAllShows()) {
                dos.writeUTF(item);
            }

            dos.flush();

        } catch (IOException e) {
            log.error("Unable to send Shows to client");
        }
    }

    private static Shows JsonWrap() {
        Shows wrapper = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Datasets/Shows.json");

            wrapper = gson.fromJson(reader, Shows.class);

            reader.close();
        } catch (Exception e) {
            log.error("Unable to read Shows.json");
        }
        return wrapper;
    }
}