package com.app.Categories;

import com.app.Identification.Recommended;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

public class RecommendedAccess {
    private static final Logger log = LogManager.getLogger(RecommendedAccess.class);

    public static void SendRecommended (Socket socket) {
        Recommended recommended = JsonWrap();
        SendMoviesToClient(socket, recommended);
    }

    private static void SendMoviesToClient (Socket socket, Recommended recommended){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(recommended.getAllRecommended().length);

            for (String item : recommended.getAllRecommended()) {
                dos.writeUTF(item);
            }

            dos.flush();
        } catch (IOException e) {
            log.error("Unable to send recommended to client");
        }
    }

    private static Recommended JsonWrap() {
        Recommended wrapper = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Recommended.json");

            wrapper = gson.fromJson(reader, Recommended.class);

            reader.close();
        } catch (Exception e) {
            log.error("Unable to read Recommended.json");
        }
        return wrapper;
    }
}
