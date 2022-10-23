package com.mfvanek.hibernate.utils;

import com.mfvanek.hibernate.entities.TestEvent;
import com.mfvanek.hibernate.entities.TestEventInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class RowsCountValidator {

    private final SessionFactory sessionFactory;
    private final Map<Class<?>, RowsCount<?>> counters;

    public RowsCountValidator(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.counters = new HashMap<>();
        add(TestEvent.class);
        add(TestEventInfo.class);
    }

    private <T> void add(Class<T> clazz) {
        this.counters.put(clazz, new RowsCount<>(sessionFactory, clazz));
    }

    public <T> void validate(final long expected, Class<T> clazz) {
        final RowsCount<?> counter = this.counters.get(clazz);
        if (counter != null) {
            counter.validate(expected);
        } else {
            throw new NoSuchElementException("Not found " + clazz.getName());
        }
    }

    private static class RowsCount<T> {

        private final SessionFactory sessionFactory;
        private final Class<T> clazz;
        private final long rowsBefore;
        private long rowsAfter;

        RowsCount(final SessionFactory sessionFactory, Class<T> clazz) {
            this.sessionFactory = sessionFactory;
            this.clazz = clazz;
            this.rowsBefore = countTotal();
        }

        private void calcAfter() {
            this.rowsAfter = countTotal();
            this.printCount(this.rowsAfter, false);
        }

        void validate(final long expected) {
            printCount(this.rowsBefore, true);
            calcAfter();
            final long actual = rowsAfter - rowsBefore;
            if (expected != actual) {
                throw new RuntimeException(String.format("Invalid inserted rows count: expected = %d, but was = %d", expected, actual));
            }
        }

        private long countTotal() {
            long rowsCount;
            try (Session session = sessionFactory.openSession()) {
                Long result = session.createQuery(String.format("select count(*) from %s", this.clazz.getName()), Long.class).getSingleResult();
                rowsCount = result != null ? result : 0;
            }
            return rowsCount;
        }

        private void printCount(long rowsCount, boolean isBefore) {
            System.out.printf("%s: rows count %s = %d%n", this.clazz.getName(), isBefore ? "before" : "after", rowsCount);
        }
    }
}
