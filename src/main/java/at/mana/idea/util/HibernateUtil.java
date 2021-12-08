package at.mana.idea.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

        private static SessionFactory sessionFactory;

        public static SessionFactory getSessionFactory(){
            if( sessionFactory == null )
                sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            return sessionFactory;
        }

        public static void closeSessionFactory() {
            if( sessionFactory != null ) {
                sessionFactory.close();
            }
            sessionFactory = null;
        }

        private static Session session;

        public static Session getSession() {
            if( session == null ) {
                session = getSessionFactory().openSession();
            }
            return session;
        }

        public static void closeSession() {
            if( session != null )
                session.close();
            session = null;
        }

        public static Session getCurrentSession() {
            return getSessionFactory().getCurrentSession();
        }

    public static <T> T executeInTransaction( ActionCallback<T> callback) {
        boolean useOpenSession = false;
        if (session == null)
            session = getSession();
        else
            useOpenSession = true;

        Transaction tx = null;
        if (!session.getTransaction().isActive()) {
            tx = session.beginTransaction();
        }

        try {
            return callback.execute(session);
        } finally {
            if (tx != null) {
                tx.commit();
            }
            if (!useOpenSession) closeSession();
        }
    }

    public interface ActionCallback<T>{
            T execute( Session session );
    }

}