package za.co.OO7J.utils;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import za.co.OO7J.AtomicPart;
import za.co.OO7J.BaseAssembly;
import za.co.OO7J.Module;

public class NewJPAQueries {

    public static Module selectOneModuleUsingKey(EntityManager session, int id) {

        System.out.println("selecting a module with id: " + id);

        if (session != null) {
            javax.persistence.Query q = session.createQuery("select m from Module m where m.designId = " + id);
            //PJWD: we don't catch NonUniqueResultException, as this should not happen
            //although pvz checks size > 0 !
            try {
                return (Module) q.getSingleResult();
            } catch (javax.persistence.NoResultException e) {
                System.out.println("*** NoResultException in selectOneModuleUsingKey");
                return null;
            }
        }

        return null; //dummy
    }

    /**
     *
     * Find all modules in the db
     *
     * Used in Q5
     *
     * note: got an error: LazyInitializationException: could not initialize
     * proxy - the owning Session was closed Fixed this by not closing the
     * session here but vlosing it in Query 5!
     *
     * @return 06-May-2006
     */
    public static List selectAllModules(EntityManager session) {
        try {

            List listResult = null;

            if (session != null) {
                listResult = session.createQuery("select m from Module m").getResultList();
            }

            return listResult;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

        return null;
    }

    public static AtomicPart selectOneAtomicPartUsingKey(EntityManager session, int id) {

        if (session != null) {
            javax.persistence.Query q = session.createQuery(
                    "select atomic from AtomicPart atomic " + "where atomic.designId = "
                    + id);
            return (AtomicPart) q.getSingleResult();
        }
        return null; //dummy
    }

    public static BaseAssembly selectOneBaseAssemblyUsingKey(EntityManager session, int id) {

        if (session != null) {
            javax.persistence.Query q = session.createQuery(
                    "select ba from BaseAssembly ba " + "where ba.designId = " + id);
            return (BaseAssembly) q.getSingleResult();
        }

        return null; //dummy

    }

    public static List selectAllBaseAssemblies(EntityManager session) {

        List listResult = null;

        if (session != null) {
            listResult = session.createQuery("select ba from BaseAssembly ba").getResultList();
        }
        // closeSessionWithTransaction();

        return listResult;

    }

    /**
     * Returns all the AtomicParts in the db
     *
     * Used in Q7
     *
     * @return 06-May-2006
     */
    public static List selectAllAtomicParts(EntityManager session) {
        try {

            List listResult = null;

            if (session != null) {
                listResult = session.createQuery("select a from AtomicPart a").getResultList();
            }
            // closeSessionWithTransaction();

            return listResult;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List selectAllAtomicPartsPaged(EntityManager session, int start, int chunk) {
        try {
            List listResult = null;
            if (session != null) {
                javax.persistence.Query query = session.createQuery("select a from AtomicPart a");
                query = query.setFirstResult(start);
                query = query.setMaxResults(chunk);
                listResult = query.getResultList();
            }

            return listResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List selectAllAtomicPartsAndAssociatedDocs(EntityManager session) {
        try {

            List listResult = null;

            if (session != null) {
                listResult = session.createQuery(
                        "select doc, part from Document doc, AtomicPart part where doc.docId = part.docId ").getResultList();
            }
            // closeSessionWithTransaction();

            return listResult;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List selectAllAtomicPartsAndAssociatedDocsPaged(EntityManager session, int start, int chunk) {
        try {

            if (session != null) {
                javax.persistence.Query query = session.createQuery(
                        "select doc, part from Document doc, AtomicPart part where doc.docId = part.docId ");
                query = query.setFirstResult(start);
                query = query.setMaxResults(chunk);
                return query.getResultList();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * Find atomic parts with build date in range:
     *
     * lowerDate <= build date <= MaxAtomicDate
     *
     * @param min
     * @param max
     * @return 06-May-2006
     */
    public static List selectAtomicPartsInRange(EntityManager session, long min, long max) {
        try {

            List listResult = null;

            if (session != null) {
                listResult = session.createQuery(
                        "select atomic from AtomicPart atomic "
                        + "where atomic.buildDate >= " + min
                        + " and atomic.buildDate <= " + max).getResultList();
            }
            // closeSessionWithTransaction();

            if (listResult != null) {
                return listResult;
            } else {
                return new ArrayList();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     *
     * Find document with this title
     *
     * Used in Query 4
     *
     * @param title
     * @return 06-May-2006
     */
    public static Object selectDocumentWithTitle(EntityManager session, String title) {
        try {

            if (SettingsUtil.debugMode) {
                System.out.println("in selectDocumentWithTitle with title: "
                        + title);
            }

            if (session != null) {
                javax.persistence.Query q = session.createQuery(
                        "select document from Document document " + "where document.title = '" + title
                        + "'");
                try {
                    return q.getSingleResult();
                } catch (javax.persistence.NoResultException e) {
                    System.out.println("--- NoResultException in selectDocumentWithTitle");
                    return null;
                }
            }
            // closeSessionWithTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     *
     * Find document with this id
     *
     * Used in Query 8
     *
     * Note on Hibernate: docid in query is not the same as docId. Needs to be
     * using Beans type names.
     *
     * @param title
     * @return 06-May-2006
     */
    public static Object selectDocumentWithId(EntityManager session, int id) {
        try {

            if (SettingsUtil.debugMode) {
                System.out.println("in selectDocumentWithTitle with id: " + id);
            }

            if (session != null) {
                 javax.persistence.Query q = session.createQuery("select document from Document document " + "where document.docId = " + id);
                 try {
                        return q.getSingleResult();
                 } catch (javax.persistence.NoResultException e){
                        System.out.println("*** NoResultException in selectDocumentWithId");
                        return null;
                 }
            }
            // closeSessionWithTransaction();

            return null; //dummy

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     *
     * Find CompositePart with this id
     *
     * Used in Delete
     *
     *
     * TODO:NOTE PVZ: note: got an error: LazyInitializationException: could not
     * initialize proxy - the owning Session was closed Fixed this by not
     * closing the session IN findCompositePart BUT closing it here in Delete!
     *
     * Got this error when I called: doc.setPart(null);
     *
     * ch 19.1.4: Initializing collections and proxies
     *
     * These same happed for Query5
     *
     *
     * @param id
     * @return 06-May-2006
     */
    public static Object selectCompositePartWithId(EntityManager session, int id) {
        try {

            if (SettingsUtil.debugMode) {
                System.out.println("in selectDocumentWithTitle with id: " + id);
            }

            if (session != null) {
                javax.persistence.Query q = session.createQuery(
                        "select comp from CompositePart comp "
                        + "where comp.designId = " + id);
                try {
                    return q.getSingleResult();
                } catch (javax.persistence.NoResultException e) {
                    System.out.println("--- NoResultException in selectCompositePartWithId");
                    return null;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;

    }
}
