package com.mfvanek.hibernate.utils;

import com.mfvanek.hibernate.entities.TestEvent;
import com.mfvanek.hibernate.entities.TestEventInfo;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public final class SessionFactoryUtil {

    private SessionFactoryUtil() {}

    public static SessionFactory build() {
        // A SessionFactory is set up once for an application!
        final ServiceRegistry registry = ServiceRegistryUtil.build();
        try {
            return new MetadataSources(registry).
                    addAnnotatedClass(TestEvent.class).
                    addAnnotatedClass(TestEventInfo.class).
                    buildMetadata().
                    buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }
}
