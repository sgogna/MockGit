package com.sabre.sabresonic.mockserver.core.executor;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.registry.ServiceRegistry;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.servicegroup.ServiceGroup;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultServiceExecutor implements ServiceExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServiceExecutor.class);
    private ServiceRegistry serviceRegistry;

    @Override
    public void execute(
            final HttpServletRequest request,
            final HttpServletResponse response) {
        try {
            int excecuted = 0;
            final MockRequest mockRequest = MockRequest.create(request);
            for (ServiceGroup serviceGroup : serviceRegistry.getAll()) {
                final boolean canExecute = serviceGroup.canExecute(mockRequest);
                if (canExecute) {
                    excecuted++;

                    MockResponse mockResponse = execute(serviceGroup, mockRequest);
                    mockResponse.fill(response);

                    break;
                } else {
                    LOG.debug("Service " + serviceGroup.getName() + " is not supported for: " + mockRequest.getURI());
                }
            }
            if (excecuted > 1) {
                LOG.warn("Executed " + excecuted + " services for: " + mockRequest.getURI());
            }
            if (excecuted == 0) {
                LOG.warn("Not found services for: " + mockRequest.getURI());
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            write(response, ex.toString());
        }
    }

    private MockResponse execute(
            final ServiceGroup service,
            final MockRequest request) {
        LOG.debug("Executing " + service.getName()
                + " for: " + request.getURI());

        final FlowVariables pipeline = new FlowVariables();
        pipeline.put("request", request);
        service.execute(pipeline);

        final Object response = pipeline.get("response");

        MockResponse mockResponse = null;
        if (response instanceof MockResponse) {
            mockResponse = (MockResponse) response;
        }
        return mockResponse;
    }

    private void write(final HttpServletResponse response, final Object msg) {
        try {
            response.getWriter().print(msg);
        } catch (IOException ex) {
            LOG.error(null, ex);
        }
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(final ServiceRegistry registry) {
        this.serviceRegistry = registry;
    }
}
