package com.app.PicturesAndDetails;

import com.app.Identification.Media;
import com.app.Identification.MediaList;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MediaDetailsSender {
    private static final Logger log = LogManager.getLogger(MediaDetailsSender.class);

    public static void SendDetails (Socket socket, String Choice) {
        MediaList wrapper = JsonWrap();
        SendInfo(socket, wrapper, Choice);
    }

    public static void SendInfo (Socket socket, MediaList wrapper, String Choice) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            for (Media media : wrapper.getAllMedia()) {

                if (media.getTitle().equalsIgnoreCase(Choice)) {
                    out.println(media.getTitle());
                    out.println(media.getType());
                    out.println(media.getDescription());


                    if (media.getType().equalsIgnoreCase("Series")) {
                        out.println(media.getSeasons().size());
                        for (int i = 0; i < media.getSeasons().size(); i++) {
                            out.println(media.getSeasons().get(i));}
                    }
                    break;
                }
            }
            String msg = "Send details for client's choice: " + Choice;
            log.info(msg);
        } catch (Exception e) {
            log.error("Unable to send details for client's choice.");
        }
    }

    public static MediaList JsonWrap () {
        MediaList wrapper = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Media.json");

            wrapper = gson.fromJson(reader, MediaList.class);

            reader.close();
        } catch (Exception e) {
            log.error("Unable to read Media.json");
        }
        return wrapper;
    }
}
