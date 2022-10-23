package com.mfvanek.hibernate;

import com.mfvanek.hibernate.entities.TestEvent;
import com.mfvanek.hibernate.utils.SessionFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Slf4j
public class DemoFindApp {

    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        try {
            sessionFactory = SessionFactoryUtil.build();
            final TestEvent first = findById(11L);
            log.info("Inside main: {}", first);
            // First query is for reading event, and the second one is for reading info
            SessionFactoryUtil.validateQueriesCount(2);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
                log.error(e.getMessage(), e);
                if (trn.isActive()) {
                    trn.markRollbackOnly();
                }
            }
        }
        log.info("Inside findById: {}", result);
        return result;
    }
}
