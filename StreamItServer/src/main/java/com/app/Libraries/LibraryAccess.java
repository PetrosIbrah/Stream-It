package com.app.Libraries;

import com.app.Identification.Accounts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import com.app.Identification.Library;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class LibraryAccess {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("StreamIt-PR");

    public static void ReturnAllLibraryItems (Socket socket, String Username, String Password){
        String[] Library = GetFromDatabase(Username, Password);
        SendLibraryToClient(socket, Library);
    }

    public static String[] GetFromDatabase (String Username, String Password) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.username = :uname AND u.password = :pwd", Accounts.class
            );
            userQuery.setParameter("uname", Username);
            userQuery.setParameter("pwd", Password);

            List<Accounts> users = userQuery.getResultList();
            if (users.isEmpty()) {
                return new String[0];
            }

            Accounts user = users.get(0);

            TypedQuery<String> libQuery = em.createQuery(
                    "SELECT l.medianame FROM Library l WHERE l.user = :user", String.class
            );
            libQuery.setParameter("user", user);

            List<String> items = libQuery.getResultList();
            return items.toArray(new String[0]);

        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        } finally {
            em.close();
        }
    }

    public static void SendLibraryToClient (Socket socket, String[] Library){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(Library.length);

            for (String item : Library) {
                dos.writeUTF(item);
            }

            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void AddItemToLibrary (Socket socket, String Username, String Password, String Item){
        SendResult(socket, AddItem(Username, Password, Item));
    }

    public static void SendResult(Socket socket, String Msg){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String AddItem(String Username, String Password, String Item) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.username = :uname AND u.password = :pwd", Accounts.class
            );
            userQuery.setParameter("uname", Username);
            userQuery.setParameter("pwd", Password);
            List<Accounts> users = userQuery.getResultList();

            if (users.isEmpty()) {
                return "User not found.";
            }

            Accounts user = users.get(0);

            TypedQuery<Library> libQuery = em.createQuery(
                    "SELECT l FROM Library l WHERE l.user = :user AND l.medianame = :item", Library.class
            );
            libQuery.setParameter("user", user);
            libQuery.setParameter("item", Item);

            if (!libQuery.getResultList().isEmpty()) {
                return "Item already in library.";
            }

            Library libItem = new Library(Item, user);
            em.persist(libItem);

            em.getTransaction().commit();
            return "Item added successfully.";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return "Unexpected error: " + e.getMessage();
        } finally {
            em.close();
        }
    }


    public static void RemoveFromLibrary(Socket socket, String Username, String Password, String Item){
        SendResult(socket, RemoveItem(Username, Password, Item));
    }

    public static String RemoveItem(String Username, String Password, String Item) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.username = :uname AND u.password = :pwd", Accounts.class
            );
            userQuery.setParameter("uname", Username);
            userQuery.setParameter("pwd", Password);
            List<Accounts> users = userQuery.getResultList();

            if (users.isEmpty()) {
                return "User not found.";
            }

            Accounts user = users.get(0);

            TypedQuery<Library> libQuery = em.createQuery(
                    "SELECT l FROM Library l WHERE l.user = :user AND l.medianame = :item", Library.class
            );
            libQuery.setParameter("user", user);
            libQuery.setParameter("item", Item);

            List<Library> results = libQuery.getResultList();

            if (results.isEmpty()) {
                return "Item not found in library.";
            }

            Library libItem = results.get(0);

            em.remove(libItem);

            em.getTransaction().commit();
            return "Item removed successfully.";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return "Unexpected error: " + e.getMessage();
        } finally {
            em.close();
        }
    }

}
