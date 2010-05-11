package za.co.OO7J.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import za.co.OO7J.AtomicPart;
import za.co.OO7J.BaseAssembly;
import za.co.OO7J.CompositePart;
import za.co.OO7J.Document;
import za.co.OO7J.Module;

public class NewPersistence {

    private static EntityManager session = null;

    //PJWD: de manier waarop Persistence verweven is met JPAUtil / HibernateUtil
    //is niet al te net
    //======================================================================
    public static EntityManager getSession() {
        return session;
    }

    public static void openPersistence() {
        if (session == null || !session.isOpen()) {
            System.out.println("Obtain a new session");
            session = NewJPAUtil.getSession();
            //fixing bug for TopLink
            session.setFlushMode(FlushModeType.COMMIT);
        }
    }

    public static void startSessionWithTransaction() {
        //System.out.println("~~~ startSessionWithTransaction ~~~");
        if (session == null || !session.isOpen()) {
            openPersistence();
        }

        try {
            session.getTransaction().begin();
        } catch (IllegalStateException e) {
            System.out.println("startSessionWithTransaction: got exception: " + e.getMessage());
            System.out.println("Assuming existing...");

        }

    }

    public static void closeSessionFactory() {
        NewJPAUtil.getSessionFactory().close();
    }

    public static void commitTransaction() {
        session.getTransaction().commit();
        session.clear(); //PJWD !!!!!!!
    }

    public static void closeSession() {
        if (session.isOpen()) {
            session.close();
        }
    }

    //======================================================================
    //TODO: SAVEORUPDATE VERDER ONDERZOEKEN!!!
    //PJWD: all object-specific methods replaced by general ones
    //PJWD: JPA heeft geen saveOrUpdate
    //see http://blog.xebia.com/2009/03/23/jpa-implementation-patterns-saving-detached-entities/
    public static void saveOrUpdateDelete(Object object) {
        //PJWD: was update
        session.persist(object);
    }

    public static void save(Object object) {
        //PJWD: was save
        session.persist(object);
    }

    public static void update(Object object) {
        //PJWD: was update
        session.persist(object);
    }

    public static void saveOrUpdate(Object object) {
        //PJWD: was saveOrUpdate
        session.persist(object);
        //session.merge(object);
    }

    public static void delete(Object object) {
        //PJWD: was delete
        session.remove(object);
    }

    //======================================================================
   	public static Module findOneModule(int moduleId) {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectOneModuleUsingKey(session, moduleId);
            } else {
                return NewJPAQueries.selectOneModuleUsingKey(session, moduleId);
            }
	}

	/**
	 * Used in Insert
	 */
	public static BaseAssembly findOneBaseAssembly(int baseAssemblyId) {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectOneBaseAssemblyUsingKey(session,baseAssemblyId);
            } else {
                return NewJPAQueries.selectOneBaseAssemblyUsingKey(session,baseAssemblyId);
            }
	}

	/**
	 * Used in Query1
	 */
	public static AtomicPart findAtomicPart(int id) {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectOneAtomicPartUsingKey(session, id);
            } else {
                return NewJPAQueries.selectOneAtomicPartUsingKey(session, id);
            }
	}

	/**
	 * Used in Query2+3
	 */
	public static List findAtomicPartsInBuildDateRange(long min, long max) {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectAtomicPartsInRange(session, min,
                                    max);
            } else {
                return NewJPAQueries.selectAtomicPartsInRange(session, min, max);
            }

	}

	/**
	 * Used in Query4
	 * Maybe use the word "find" rather that "select"
	 * should only be one match
	 */
	public static Document findDocumentWithTitle(String title) {
            if (SettingsUtil.annotationConfiguration) {
                return (Document) NewJPANamedQueries.selectDocumentWithTitle(session, title);
            } else {
                return (Document) NewJPAQueries.selectDocumentWithTitle(session, title);
            }
	}

	/**
	 * Used in Query5
	 */
	public static List findAllModules() {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectAllModules(session);
            } else {
                return NewJPAQueries.selectAllModules(session);
            }
	}

	/**
	 * Used in Query7
	 */
	public static List findAllAtomicParts() {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectAllAtomicParts(session);
            } else {
                return NewJPAQueries.selectAllAtomicParts(session);
            }
	}

        public static List findAllAtomicPartsPaged(int start, int chunk) {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectAllAtomicPartsPaged(session, start, chunk);
            } else {
                return NewJPAQueries.selectAllAtomicPartsPaged(session, start, chunk);
            }
	}

	/**
	 * Used in Query8
	 */
	public static Document findDocumentWithId(int docId) {
            if (SettingsUtil.annotationConfiguration) {
                return (Document) NewJPANamedQueries.selectDocumentWithId(session, docId);
            } else {
                return (Document) NewJPAQueries.selectDocumentWithId(session,docId);
            }
	}

	/**
	 * Used in Query8
	 */
	public static List findAtomicPartsAndAssociatedDocs() {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectAllAtomicPartsAndAssociatedDocs(session);
            } else {
                return NewJPAQueries.selectAllAtomicPartsAndAssociatedDocs(session);
            }
	}

        public static List findAtomicPartsAndAssociatedDocsPaged(int start, int chunk) {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectAllAtomicPartsAndAssociatedDocsPaged(session, start, chunk);
            } else {
                return NewJPAQueries.selectAllAtomicPartsAndAssociatedDocsPaged(session, start, chunk);
            }
	}

	/**
	 * Used in Delete
	 *
	 * should only be one match
	 */
	public static CompositePart findCompositePart(int id) {
            if (SettingsUtil.annotationConfiguration) {
                return (CompositePart) NewJPANamedQueries.selectCompositePartWithId(session, id);
            } else {
                return (CompositePart) NewJPAQueries.selectCompositePartWithId(session, id);
            }
	}

	public static List findAllBaseAssemblies() {
            if (SettingsUtil.annotationConfiguration) {
                return NewJPANamedQueries.selectAllBaseAssemblies(session);
            } else {
                return NewJPAQueries.selectAllBaseAssemblies(session);
            }
	}
}
