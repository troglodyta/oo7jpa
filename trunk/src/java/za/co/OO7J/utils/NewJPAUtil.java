package za.co.OO7J.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class NewJPAUtil {

    static private EntityManagerFactory emf;
    static private EntityManager em;
    private final static String PERSISTENCE_UNIT_NAME = "persunit";

    //PJWD: Annotations are now set in persistence.xml
    static {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();
    }

    public static EntityManagerFactory getSessionFactory() {
        if (emf == null || emf.isOpen() == false) {
            try {
               emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            } catch (Throwable ex) {
                ex.printStackTrace();
                throw new ExceptionInInitializerError(ex);
            }
        }
        return emf;
    }

    public static EntityManager getSession() {
        if (em == null || !em.isOpen()) {
            // TODO tom: log.debug("Opening new Session for this thread.");
            em = getSessionFactory().createEntityManager();
        }
        return em;
    }
}
