package com.mfvanek.hibernate.utils;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class MultiTenantProvider extends AbstractMultiTenantConnectionProvider {

    // public static final MultiTenantConnectionProvider INSTANCE = new MultiTenantConnectionProvider();

    private final Map<String, ConnectionProvider> connectionProviderMap =
            new HashMap<>();

    Map<String, ConnectionProvider> getConnectionProviderMap() {
        return connectionProviderMap;
    }

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        // return connectionProviderMap.get(TenantContext.DEFAULT_TENANT_IDENTIFIER);
        throw new NotImplementedException();
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(
            String tenantIdentifier) {
        return connectionProviderMap.get(
                tenantIdentifier
        );
    }
}