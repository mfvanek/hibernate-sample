package com.mfvanek.hibernate.utils;

import com.mfvanek.hibernate.entities.TestEvent;
import com.mfvanek.hibernate.entities.TestEventInfo;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.service.ServiceRegistry;

import java.util.concurrent.atomic.AtomicLong;

public final class SessionFactoryUtil {

    private SessionFactoryUtil() {}

    private static final AtomicLong counter = new AtomicLong();

    public static SessionFactory build() {
        resetQueriesCount();

        // A SessionFactory is set up once for an application!
        final ServiceRegistry registry = ServiceRegistryUtil.build();
        try {
            return new MetadataSources(registry).
                    addAnnotatedClass(TestEvent.class).
                    addAnnotatedClass(TestEventInfo.class).
                    buildMetadata().
                    getSessionFactoryBuilder().
                    applyStatementInspector((StatementInspector) sql -> {
                        counter.incrementAndGet();
                        return sql;
                    }).
                    build();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

    private static void resetQueriesCount() {
        counter.set(0L);
    }

    private static long getQueriesCount() {
        return counter.get();
    }

    public static void validateQueriesCount(long expected) {
        final long actual = getQueriesCount();
        if (expected != actual) {
            throw new RuntimeException(String.format("Invalid queries count: expected = %d, but was = %d", expected, actual));
        }
    }
}
