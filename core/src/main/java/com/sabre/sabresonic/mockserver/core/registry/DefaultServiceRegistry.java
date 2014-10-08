package com.sabre.sabresonic.mockserver.core.registry;

import com.sabre.sabresonic.mockserver.core.factory.ServicesProviderFactory;
import com.sabre.sabresonic.mockserver.core.servicegroup.ServiceGroup;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public final class DefaultServiceRegistry implements ServiceRegistry {

    static class SingletonHolder {
        static final DefaultServiceRegistry INSTANCE = new DefaultServiceRegistry();
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    
    private final Map<String, ServiceGroup> services = new ConcurrentHashMap();

    public static DefaultServiceRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private DefaultServiceRegistry() {
        ServicesProviderFactory.registerServiceProvider(this);
    }

    @Override
    public Collection<ServiceGroup> getAll() {
        return new HashMap(services).values();
    }

    @Override
    public ServiceGroup get(final String serviceName) {
        for (Map.Entry<String, ServiceGroup> e : services.entrySet()) {
            if (e.getValue().getName().equals(serviceName)) {
                return e.getValue();
            }
        }
        return null;
    }

    @Override
    public void put(final String serviceName, final ServiceGroup serviceGroup) {
        services.put(serviceName, serviceGroup);
        LOG.debug("Added (overwritten) service to registry: " + serviceName);
    }

    @Override
    public void remove(final String serviceName) {
        services.remove(serviceName);
        LOG.debug("Removed " + serviceName + " from registry.");
    }
}
