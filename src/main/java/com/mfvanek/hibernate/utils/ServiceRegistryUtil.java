package com.mfvanek.hibernate.utils;

import com.mfvanek.hibernate.consts.Const;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public final class ServiceRegistryUtil {

    private ServiceRegistryUtil() {}

    public static ServiceRegistry build() {
        return new StandardServiceRegistryBuilder()
                .configure(Const.HIBERNATE_CONFIG_FILE_NAME)
                .loadProperties(Const.PROPERTY_FILE_NAME)
                .build();
    }
}
