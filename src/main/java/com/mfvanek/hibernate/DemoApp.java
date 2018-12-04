package com.mfvanek.hibernate;

import com.mfvanek.hibernate.enums.TestEventType;
import com.mfvanek.hibernate.models.TestEvent;
import com.mfvanek.hibernate.models.TestEventInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DemoApp {

    private static final Logger logger = LoggerFactory.getLogger(DemoApp.class);
    private static SessionFactory sessionFactory;
    private static final int LOOP_COUNT = 1_000;

    /**
     * There is another approach
     * https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#multitenacy
     */
    //private static final String SCHEMA_NAME = "alien";

    public static void main(String[] args) {
        try {
            init();
            saveFromCurrentThread();
            saveFromNewSingleThread();
            saveUsingThreadPool();
            countTotal();
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

    private static void saveItem() {
        // try (Session session = sessionFactory.withOptions().tenantIdentifier(SCHEMA_NAME).openSession()) {
        try (Session session = sessionFactory.openSession()) {
            Transaction trn = session.beginTransaction();
            try {
                final TestEvent firstEvent = new TestEvent("Our very first event!", new Date());
                addTestEventInfo(firstEvent, 11);
                session.save(firstEvent);

                final TestEvent secondEvent = new TestEvent("A follow up event", new Date());
                addTestEventInfo(secondEvent, 22);
                session.save(secondEvent);
                trn.commit();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                trn.markRollbackOnly(); // ??? TODO
            }
        }
    }

    private static void addTestEventInfo(TestEvent event, int number) {
        final Set<TestEventInfo> info = new HashSet<>(Arrays.asList(
                new TestEventInfo(event, TestEventType.MAIN, String.format("%d, first, main", number)),
                new TestEventInfo(event, TestEventType.ADDITIONAL, String.format("%d, second, additional", number)),
                new TestEventInfo(event, TestEventType.EXTENDED, String.format("%d, third, ext", number))));
        event.setInfo(info);
    }

    private static void countTotal() {
        try (Session session = sessionFactory.openSession()) {
            Long rowsCount = session.createQuery("select count(*) from TestEvent", Long.class).getSingleResult();
            System.out.println("rows count = " + (rowsCount != null ? rowsCount : 0));
        }
    }

    private static void saveFromCurrentThread() {
        new DataSaver("current thread").run();
    }

    private static void saveFromNewSingleThread() {
        try {
            final Thread thread = new Thread(new DataSaver("new single thread"), "pg_single");
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void saveUsingThreadPool() {
        final String message = "Saving items using thread pool";
        System.out.println(message);
        final long timeStart = System.currentTimeMillis();
        try {
            final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
            // final ExecutorService threadPool = Executors.newFixedThreadPool(10);
            for (int i = 0; i < LOOP_COUNT; ++i) {
                threadPool.submit(DemoApp::saveItem);
            }
            threadPool.shutdown();
            threadPool.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        System.out.println(String.format("%s. Completed. Time elapsed = %d (ms)",
                message, System.currentTimeMillis() - timeStart));
    }

    private static class DataSaver implements Runnable {

        private final String message;

        DataSaver(String message) {
            this.message = "Saving items from " + message;
        }

        @Override
        public void run() {
            System.out.println(this.message);
            final long timeStart = System.currentTimeMillis();
            for (int i = 0; i < LOOP_COUNT; ++i) {
                saveItem();
            }
            System.out.println(String.format("%s. Completed. Time elapsed = %d (ms)",
                    this.message, System.currentTimeMillis() - timeStart));
        }
    }
}
