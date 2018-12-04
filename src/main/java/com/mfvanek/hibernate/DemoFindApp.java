package com.mfvanek.hibernate;

import com.mfvanek.hibernate.models.TestEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoFindApp {

    private static final Logger logger = LoggerFactory.getLogger(DemoFindApp.class);
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        try {
            init();
            final TestEvent first = findById(11L);
            //System.out.println(first);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
                sessionFactory = null;
            }
        }
    }

    private static void init() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static TestEvent findById(Long id) {
        TestEvent result = new TestEvent();
        try (Session session = sessionFactory.openSession()) {
            Transaction trn = session.beginTransaction();
            try {
                result = session.get(TestEvent.class, id);
                System.out.println(result);
                trn.commit();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                trn.markRollbackOnly(); // ??? TODO
            }
        }
        // LazyInitializationException will be thrown
        // https://vladmihalcea.com/the-best-way-to-handle-the-lazyinitializationexception/
        //System.out.println(result);
        return result;
    }
}
