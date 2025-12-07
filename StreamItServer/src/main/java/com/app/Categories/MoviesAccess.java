package com.app.Categories;

import com.app.Identification.Movies;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MoviesAccess {
    private static final Logger log = LogManager.getLogger(MoviesAccess.class);

    public static void SendMovies (SSLSocket socket) {
        Movies movies = JsonWrap();
        SendMoviesToClient(socket, movies);
    }

    private static void SendMoviesToClient (SSLSocket socket, Movies movies){
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
            FileReader reader = new FileReader("Datasets/Movies.json");

            wrapper = gson.fromJson(reader, Movies.class);

            reader.close();
        } catch (Exception e) {
            log.error("Unable to read Movies.json");
        }
        return wrapper;
    }
}