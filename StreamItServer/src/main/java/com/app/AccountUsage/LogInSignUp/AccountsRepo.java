package com.app.AccountUsage.LogInSignUp;

import com.app.Utilization.JwtUtil;
import jakarta.persistence.*;
import com.app.Identification.Accounts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AccountsRepo {
    private static final Logger log = LogManager.getLogger(AccountsRepo.class);

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("StreamIt-PR");

    public static String SaveAccount(String username, String email, String password) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT 1 FROM Accounts a WHERE a.username = :uname", Integer.class
            );
            query.setParameter("uname", username);
            query.setMaxResults(1);

            if (!query.getResultList().isEmpty()) {
                return "Username already exists";
            }

            String token = JwtUtil.generateToken(username, email, password);

            em.getTransaction().begin();
            Accounts account = new Accounts(username, email);
            account.setToken(token);
            em.persist(account);
            em.getTransaction().commit();

            return "Account Created";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return "Unexpected Error";
        } finally {
            em.close();
        }
    }

    public static String CheckLogIn(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Accounts> query = em.createQuery(
                    "SELECT a FROM Accounts a WHERE a.username = :uname", Accounts.class
            );
            query.setParameter("uname", username);
            List<Accounts> results = query.getResultList();

            if (!results.isEmpty()) {
                Accounts account = results.getFirst();
                if (JwtUtil.verifyPassword(account.getToken(), password)) {
                    log.info("Log In Accepted");
                    return "Log In Accepted";
                } else {
                    log.info("Wrong password");
                    return "Log In NOT Accepted";
                }
            } else {
                log.info("Log In NOT Accepted");
                return "Log In NOT Accepted";
            }
        } catch (Exception e) {
            log.error("Unexpected Log in Error");
            return "Unexpected Error";
        }
    }
}