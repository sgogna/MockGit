package com.sabre.sabresonic.mockserver.core.registry;

import com.sabre.sabresonic.mockserver.core.servicegroup.ServiceGroup;
import java.util.Collection;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public interface ServiceRegistry {

    Collection<ServiceGroup> getAll();

    ServiceGroup get(String serviceName);

    void put(String serviceName, ServiceGroup serviceGroup);

    void remove(String serviceName);
}
