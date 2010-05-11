/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.OO7J.utils;

import javax.persistence.*;

/**
 *
 * @author PJWD
 */
public class JPAUtil_threaded {

    private final static String PERSISTENCE_UNIT_NAME = "persunit";
    private static EntityManagerFactory entityManagerFactory;
    private static ThreadLocal threadEntityManager = new ThreadLocal();
    private static ThreadLocal threadEntityTransaction = new ThreadLocal();

    // Create the initial EntityManagerFactory with default configuration
    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(
                    PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    //PJWD: wrapper methods
    public static EntityManagerFactory getSessionFactory() {
        return getEntityManagerFactory();
    }

    public static EntityManager getSession() {
        return getEntityManager();
    }

    /**
     * Returns the EntityManagerFactory used for this static class.
     *
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || entityManagerFactory.isOpen() == false) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory(
                        PERSISTENCE_UNIT_NAME);
            } catch (Throwable ex) {
                ex.printStackTrace();
                throw new ExceptionInInitializerError(ex);
            }
        }
        return entityManagerFactory;
    }

    /**
     * Retrieves the current EntityManager local to the thread.
     * If no Session is open, opens a new EntityManager for the running thread.
     *
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        EntityManager em = (EntityManager) threadEntityManager.get();
        if (em == null || !em.isOpen()) {
            // TODO tom: log.debug("Opening new Session for this thread.");
            em = getEntityManagerFactory().createEntityManager();
            threadEntityManager.set(em);
        }
        return em;
    }

    /**
     * Closes the EntityManager local to the thread.
     */
    public static void closeEntityManager() {
        EntityManager em = (EntityManager) threadEntityManager.get();
        threadEntityManager.set(null);
        if ((null != em) && em.isOpen()) {
            // TODO tom: log.debug("Closing Session of this thread.");
            em.close();
        }
    }

    /**
     * Start a new database entity transaction.
     */
    public static void beginEntityTransaction() {
        EntityTransaction et = (EntityTransaction) threadEntityTransaction.get();
        if (null == et) {
            et = getEntityManager().getTransaction();
            et.begin();
            threadEntityTransaction.set(et);
        }
    }

    /**
     * Commit the database entity transaction.
     */
    public static void commitEntityTransaction() {
        EntityTransaction et = (EntityTransaction) threadEntityTransaction.get();
        try {
            if ((null != et) && et.isActive()) {
                et.commit();
            }
            threadEntityTransaction.set(null);
        } catch (RollbackException ex) {
            rollbackEntityTransaction();
            throw ex;
        }
    }

    /**
     * Rollback the database entity transaction.
     */
    public static void rollbackEntityTransaction() {
        EntityTransaction et = (EntityTransaction) threadEntityTransaction.get();
        try {
            threadEntityTransaction.set(null);
            if ((null != et) && et.isActive()) {
                // TODO tom: log.debug("Tyring to rollback database transaction of this thread.");
                et.rollback();
            }
        } finally {
            closeEntityManager();
        }
    }
}
