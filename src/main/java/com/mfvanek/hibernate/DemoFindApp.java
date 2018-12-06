package com.mfvanek.hibernate;

import com.mfvanek.hibernate.entities.TestEvent;
import com.mfvanek.hibernate.utils.SessionFactoryUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoFindApp {

    private static final Logger logger = LoggerFactory.getLogger(DemoFindApp.class);
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        try {
            sessionFactory = SessionFactoryUtil.build();
            final TestEvent first = findById(11L);
            System.out.println("Inside main: " + first);
            // First query is for reading event, and the second one is for reading info
            SessionFactoryUtil.validateQueriesCount(2);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
                sessionFactory = null;
            }
        }
    }

    private static TestEvent findById(Long id) {
        TestEvent result = new TestEvent();
        try (Session session = sessionFactory.openSession()) {
            Transaction trn = session.beginTransaction();
            try {
                result = session.get(TestEvent.class, id);
                System.out.println("Inside Session: " + result);
                // In order to avoid LazyInitializationException
                Hibernate.initialize(result);
                trn.commit();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                if (trn.isActive()) {
                    trn.markRollbackOnly();
                }
            }
        }
        System.out.println("Inside findById: " + result);
        return result;
    }
}
