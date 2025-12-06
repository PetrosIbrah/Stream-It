package com.app.Categories;

import com.app.Identification.Movies;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

public class MoviesAccess {
    private static final Logger log = LogManager.getLogger(MoviesAccess.class);

    public static void SendMovies (Socket socket) {
        Movies movies = JsonWrap();
        SendMoviesToClient(socket, movies);
    }

    private static void SendMoviesToClient (Socket socket, Movies movies){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(movies.getAllMovies().length);

            for (String item : movies.getAllMovies()) {
                dos.writeUTF(item);
            }

            dos.flush();

        } catch (IOException e) {
            log.error("Unable to send movies to client");
        }
    }

    private static Movies JsonWrap() {
        Movies wrapper = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Movies.json");

            wrapper = gson.fromJson(reader, Movies.class);

            reader.close();
        } catch (Exception e) {
            log.error("Unable to read Movies.json");
        }
        return wrapper;
    }
}
