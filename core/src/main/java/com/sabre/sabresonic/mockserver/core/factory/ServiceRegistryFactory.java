package com.sabre.sabresonic.mockserver.core.factory;

import com.sabre.sabresonic.mockserver.core.registry.DefaultServiceRegistry;
import com.sabre.sabresonic.mockserver.core.registry.ServiceRegistry;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public final class ServiceRegistryFactory {

    private ServiceRegistryFactory() {
    }

    public static ServiceRegistry createServiceRegistry() {
        return DefaultServiceRegistry.getInstance();
    }

}
