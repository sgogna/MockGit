package com.sabre.sabresonic.mockserver.core.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService implements Service {

    protected final Logger getLogger() {
        return LoggerFactory.getLogger(getClass().getName());
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        getLogger().debug("Invoking service: " + this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
