package com.mfvanek.hibernate;

import com.mfvanek.hibernate.entities.TestEvent;
import com.mfvanek.hibernate.entities.TestEventInfo;
import com.mfvanek.hibernate.enums.TestEventType;
import com.mfvanek.hibernate.utils.RowsCountValidator;
import com.mfvanek.hibernate.utils.SessionFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DemoInsertApp {

    private static SessionFactory sessionFactory;
    private static final int LOOP_COUNT = 1_000;

    public static void main(String[] args) {
        try {
            sessionFactory = SessionFactoryUtil.build();
            final RowsCountValidator validator = new RowsCountValidator(sessionFactory);

            saveFromCurrentThread();
            saveFromNewSingleThread();
            saveUsingThreadPool();

            final long EXPECTED_EVENTS_COUNT = 3 * 2 * LOOP_COUNT;
            validator.validate(EXPECTED_EVENTS_COUNT, TestEvent.class);
            validator.validate(3 * EXPECTED_EVENTS_COUNT, TestEventInfo.class);
            SessionFactoryUtil.validateQueriesCount(12 * LOOP_COUNT + 4);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
                sessionFactory = null;
            }
        }
    }

    private static void saveItem() {
        try (Session session = sessionFactory.openSession()) {
            Transaction trn = session.beginTransaction();
            try {
                final TestEvent firstEvent = new TestEvent("Our very first event!", new Date());
                addTestEventInfo(firstEvent, 11);
                session.persist(firstEvent);

                final TestEvent secondEvent = new TestEvent("A follow up event", new Date());
                addTestEventInfo(secondEvent, 22);
                session.persist(secondEvent);
                trn.commit();
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                if (trn.isActive()) {
                    trn.markRollbackOnly();
                }
            }
        }
    }

    private static void addTestEventInfo(TestEvent event, int number) {
        final Set<TestEventInfo> info = new HashSet<>(Arrays.asList(
                new TestEventInfo(TestEventType.MAIN, String.format("%d, first, main", number)),
                new TestEventInfo(TestEventType.ADDITIONAL, String.format("%d, second, additional", number)),
                new TestEventInfo(TestEventType.EXTENDED, String.format("%d, third, ext", number))));
        event.addEventInfo(info);
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
            log.error(e.getMessage(), e);
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
                threadPool.submit(DemoInsertApp::saveItem);
            }
            threadPool.shutdown();
            threadPool.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        System.out.printf("%s. Completed. Time elapsed = %d (ms)%n",
                message, System.currentTimeMillis() - timeStart);
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
            System.out.printf("%s. Completed. Time elapsed = %d (ms)%n",
                    this.message, System.currentTimeMillis() - timeStart);
        }
    }
}
