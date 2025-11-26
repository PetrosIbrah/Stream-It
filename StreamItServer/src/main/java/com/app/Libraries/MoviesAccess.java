package com.app.Libraries;

import com.app.Identification.Movies;
import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

public class MoviesAccess {
    public static void SendMovies (Socket socket) {
        Movies movies = JsonWrap();
        SendMoviesToClient(socket, movies);
    }

    public static void SendMoviesToClient (Socket socket, Movies movies){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(movies.getAllMovies().length);

            for (String item : movies.getAllMovies()) {
                dos.writeUTF(item);
            }

            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Movies JsonWrap() {
        Movies wrapper = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Movies.json");

            wrapper = gson.fromJson(reader, Movies.class);

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wrapper;
    }


}
