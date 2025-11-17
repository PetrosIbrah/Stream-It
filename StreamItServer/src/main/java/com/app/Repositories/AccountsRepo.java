package  com.app.Repositories;
import jakarta.persistence.*;
import com.app.Identification.Accounts;

public class AccountsRepo {
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
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM Accounts a WHERE a.username = :uname AND a.password = :pwd",
                    Long.class
            );
            query.setParameter("uname", username);
            query.setParameter("pwd", password);

            if( query.getSingleResult() > 0){
                return "Log In Accepted";
            } else {
                return "Log In NOT Accepted";
            }
        } catch (Exception e) {
            return "Unexpected Error";
        } finally {
            em.close();
        }
    }



    public Accounts getAccountByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        Accounts account = null;
        try {
            TypedQuery<Accounts> query = em.createQuery("SELECT a FROM Accounts a WHERE a.username = :uname", Accounts.class);
            query.setParameter("uname", username);
            account = query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
        return account;
    }
}
