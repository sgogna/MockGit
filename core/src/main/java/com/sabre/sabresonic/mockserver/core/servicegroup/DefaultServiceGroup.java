package com.sabre.sabresonic.mockserver.core.servicegroup;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.matcher.SimpleMatcher;
import com.sabre.sabresonic.mockserver.core.matcher.StringMatcher;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.service.Service;

public class DefaultServiceGroup extends AbstractService implements ServiceGroup {

    private String name;
    private Boolean enabled;
    private StringMatcher methodPattern = new SimpleMatcher("*");
    private StringMatcher urlPattern = new SimpleMatcher("*");
    private Service service;

    public DefaultServiceGroup(final Service service) {
        super();
        this.service = service;
    }

    public DefaultServiceGroup(final Service service, final String name) {
        this(service);
        this.name = name;
    }

    public DefaultServiceGroup(final Service service, final String name, final Boolean enabled) {
        this(service, name);
        this.enabled = enabled;
    }

    @Override
    public boolean canExecute(final MockRequest request) {
        if (urlPattern != null & methodPattern != null) {
            return (enabled == null || enabled) && urlPattern.matches(request.getURI().toString()) && methodPattern.matches(request.getMethod());
        }
        return false;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        if (enabled == null || enabled) {
            service.execute(flowVariables);
        } else {
            getLogger().debug("Trying to invoke disabled service: " + getName());
        }
    }

    public void setService(final Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public StringMatcher getMethodPattern() {
        return methodPattern;
    }

    public void setMethodPattern(final StringMatcher methodPattern) {
        this.methodPattern = methodPattern;
    }

    public StringMatcher getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(final StringMatcher urlPattern) {
        this.urlPattern = urlPattern;
    }
}