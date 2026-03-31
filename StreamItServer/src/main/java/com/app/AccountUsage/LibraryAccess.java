package com.app.AccountUsage;

import com.app.Identification.Accounts;
import com.app.Utilization.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import com.app.Identification.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.SSLSocket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LibraryAccess {
    private static final Logger log = LogManager.getLogger(LibraryAccess.class);

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("StreamIt-PR");

    public void ReturnAllLibraryItems (SSLSocket socket, String Username, String Password){
        String[] Library = GetFromDatabase(Username, Password);
        SendLibraryToClient(socket, Library);
    }

    private String[] GetFromDatabase(String Username, String Password) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.username = :uname", Accounts.class
            );
            userQuery.setParameter("uname", Username);

            List<Accounts> users = userQuery.getResultList();
            if (users.isEmpty()) return new String[0];

            Accounts user = users.getFirst();

            if (!JwtUtil.verifyPassword(user.getToken(), Password)) return new String[0];

            TypedQuery<String> libQuery = em.createQuery(
                    "SELECT l.medianame FROM Library l WHERE l.user = :user", String.class
            );
            libQuery.setParameter("user", user);
            List<String> items = libQuery.getResultList();
            return items.toArray(new String[0]);
        } catch (Exception e) {
            log.error("Unable to get library items from Database");
            return new String[0];
        }
    }

    private void SendLibraryToClient (SSLSocket socket, String[] Library){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(Library.length);

            for (String item : Library) {
                dos.writeUTF(item);
            }

            dos.flush();

        } catch (IOException e) {
            log.error("Unable to communicate with the client, to send library");
        }
    }


    public void AddItemToLibrary (SSLSocket socket, String Username, String Password, String Item){
        SendResult(socket, AddItem(Username, Password, Item));
    }

    private void SendResult(SSLSocket socket, String Msg){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Msg);
        } catch (IOException e) {
            log.error("Unable to communicate with the client");
        }
    }

    public String AddItem(String Username, String Password, String Item) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.username = :uname", Accounts.class
            );
            userQuery.setParameter("uname", Username);
            List<Accounts> users = userQuery.getResultList();

            if (users.isEmpty()) {
                return "User not found.";
            }

            Accounts user = users.getFirst();

            if (!JwtUtil.verifyPassword(user.getToken(), Password)) {
                return "User not found.";
            }

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
            log.error("Unable to add item");
            return "Unable to add item";
        } finally {
            em.close();
        }
    }


    public void RemoveFromLibrary(SSLSocket socket, String Username, String Password, String Item){
        SendResult(socket, RemoveItem(Username, Password, Item));
    }

    public String RemoveItem(String Username, String Password, String Item) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.username = :uname", Accounts.class
            );
            userQuery.setParameter("uname", Username);
            List<Accounts> users = userQuery.getResultList();

            if (users.isEmpty()) {
                return "User not found.";
            }

            Accounts user = users.getFirst();

            if (!JwtUtil.verifyPassword(user.getToken(), Password)) {
                return "User not found.";
            }

            TypedQuery<Library> libQuery = em.createQuery(
                    "SELECT l FROM Library l WHERE l.user = :user AND l.medianame = :item", Library.class
            );
            libQuery.setParameter("user", user);
            libQuery.setParameter("item", Item);

            List<Library> results = libQuery.getResultList();

            if (results.isEmpty()) {
                return "Item not found in library.";
            }

            Library libItem = results.getFirst();
            em.remove(libItem);

            em.getTransaction().commit();
            return "Item removed successfully.";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.error("Unable to remove from library");
            return "Unable to remove from library";
        } finally {
            em.close();
        }
    }

}