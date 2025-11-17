package com.app;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MediaDetailsSender {

    public static void SendDetails (Socket socket, String Choice) {
        MediaList wrapper = JsonWrap();
        SendInfo(socket, wrapper, Choice);
    }

    public static void SendInfo (Socket socket, MediaList wrapper, String Choice) {
        // MediaList.setShows(wrapper);
        System.out.println(Choice);
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            for (Media media : wrapper.getShows()) {

                if (media.getTitle().equalsIgnoreCase(Choice)) {
                    out.println(media.getTitle());
                    out.println(media.getType());
                    out.println(media.getDescription());
                    // out.println(getVideoLength(Choice));


                    if (media.getType().equalsIgnoreCase("Series")) {
                        out.println(media.getSeasons().size());
                        for (int i = 0; i < media.getSeasons().size(); i++) {
                            out.println(media.getSeasons().get(i));
                            // System.out.println("Season " + (i + 1) + " has " + media.getSeasons().get(i) + " episodes");
                        }
                    }
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }


        return wrapper;
    }

}
