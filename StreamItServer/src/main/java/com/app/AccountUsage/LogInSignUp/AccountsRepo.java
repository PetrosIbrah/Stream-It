package com.app.AccountUsage.LogInSignUp;

import jakarta.persistence.*;
import com.app.Identification.Accounts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccountsRepo {
    private static final Logger log = LogManager.getLogger(AccountsRepo.class);

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("StreamIt-PR");

    public static String SaveAccount(String username, String password) {
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

            em.getTransaction().begin();
            Accounts account = new Accounts(username, password);
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
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM Accounts a WHERE a.username = :uname AND a.password = :pwd",
                    Long.class
            );
            query.setParameter("uname", username);
            query.setParameter("pwd", password);

            if (query.getSingleResult() > 0) {
                log.info("Log In Accepted");
                return "Log In Accepted";
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