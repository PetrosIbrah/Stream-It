package com.app.AccountUsage;

import javax.net.ssl.SSLSocket;
import com.app.Identification.Accounts;
import com.app.Utilization.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import com.app.Identification.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AccountSettings {
    private static final Logger log = LogManager.getLogger(AccountSettings.class);
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("StreamIt-PR");

    public void ClearLibrary(SSLSocket socket, String Username, String Password) {
        SendResult(socket, ClearLibraryAction(Username, Password));
    }

    public void DeleteAccount (SSLSocket socket, String Username, String Password) {
        SendResult(socket, DeleteAccountAction(Username, Password));
    }

    public void ChangePassword (SSLSocket socket, String Email, String OldPassword, String NewPassword) {
        SendResult(socket, ChangePasswordAction(Email, OldPassword, NewPassword));
    }

    public void ChangeUsername (SSLSocket socket, String Email, String Password, String NewUsername) {
        SendResult(socket, ChangeUsernameAction(Email, Password, NewUsername));
    }

    private String ClearLibraryAction(String Username, String Password) {
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

            em.createQuery("DELETE FROM Library l WHERE l.user = :user")
                    .setParameter("user", user)
                    .executeUpdate();

            em.getTransaction().commit();
            return "Library cleared successfully.";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.error("Unable to clear library");
            return "Unable to clear library";
        } finally {
            em.close();
        }
    }

    private String DeleteAccountAction(String Username, String Password) {
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

            em.createQuery("DELETE FROM Library l WHERE l.user = :user")
                    .setParameter("user", user)
                    .executeUpdate();

            em.remove(em.contains(user) ? user : em.merge(user));

            em.getTransaction().commit();
            return "Account deleted successfully.";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.error("Unable to delete account");
            return "Unable to delete account";
        } finally {
            em.close();
        }
    }

    private String ChangePasswordAction(String Email, String OldPassword, String NewPassword) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.email = :email", Accounts.class
            );
            userQuery.setParameter("email", Email);
            List<Accounts> users = userQuery.getResultList();

            if (users.isEmpty()) {
                return "User not found.";
            }

            Accounts user = users.getFirst();

            if (!JwtUtil.verifyPassword(user.getToken(), OldPassword)) {
                return "Wrong password.";
            }

            String newToken = JwtUtil.generateToken(user.getUsername(), user.getEmail(), NewPassword);
            user.setToken(newToken);

            em.getTransaction().commit();
            return "Password changed successfully.";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.error("Unable to change password");
            return "Unable to change password";
        } finally {
            em.close();
        }
    }

    private String ChangeUsernameAction(String Email, String Password, String NewUsername) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Accounts> userQuery = em.createQuery(
                    "SELECT u FROM Accounts u WHERE u.email = :email", Accounts.class
            );
            userQuery.setParameter("email", Email);
            List<Accounts> users = userQuery.getResultList();

            if (users.isEmpty()) {
                return "User not found.";
            }

            Accounts user = users.getFirst();

            if (!JwtUtil.verifyPassword(user.getToken(), Password)) {
                return "Wrong password.";
            }

            TypedQuery<Integer> checkQuery = em.createQuery(
                    "SELECT 1 FROM Accounts a WHERE a.username = :uname", Integer.class
            );
            checkQuery.setParameter("uname", NewUsername);
            checkQuery.setMaxResults(1);
            if (!checkQuery.getResultList().isEmpty()) {
                return "Username already exists.";
            }

            String newToken = JwtUtil.generateToken(NewUsername, user.getEmail(), Password);
            user.setUsername(NewUsername);
            user.setToken(newToken);

            em.getTransaction().commit();
            return "Username changed successfully.";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.error("Unable to change username");
            return "Unable to change username";
        } finally {
            em.close();
        }
    }

    private void SendResult(SSLSocket socket, String Msg){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Msg);
        } catch (IOException e) {
            log.error("Unable to communicate with the client");
        }
    }
}
