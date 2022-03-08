/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
        Transaction tx = null;
        if (!getCurrentSession().getTransaction().isActive()) {
            tx = getCurrentSession().beginTransaction();
        }
        try {
            return callback.execute(getCurrentSession());
        } finally {
            if (tx != null) {
                tx.commit();
            }
        }
    }

    public interface ActionCallback<T>{
            T execute( Session session );
    }

}
