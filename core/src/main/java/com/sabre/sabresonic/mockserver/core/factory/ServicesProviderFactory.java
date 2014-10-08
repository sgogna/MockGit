package com.sabre.sabresonic.mockserver.core.factory;

import com.sabre.sabresonic.mockserver.core.registry.ServiceRegistry;
import com.sabre.sabresonic.mockserver.core.registry.file.FileServicesProvider;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public final class ServicesProviderFactory {

    private ServicesProviderFactory() {
    }

    public static void registerServiceProvider(
            final ServiceRegistry serviceRegistry) {

        new FileServicesProvider(serviceRegistry);
    }
}
