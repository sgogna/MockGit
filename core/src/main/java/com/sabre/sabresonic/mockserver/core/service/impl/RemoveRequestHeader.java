package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.matcher.StringMatcher;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RemoveRequestHeader extends AbstractService {

    private String request;
    private String name;
    private StringMatcher matcher;

    public RemoveRequestHeader() {
        super();
    }

    public RemoveRequestHeader(String request) {
        this();
        this.request = request;
    }

    public RemoveRequestHeader(String name, StringMatcher matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    public void execute(FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (request == null) {
                request = "request";
            }
            MockRequest mockRequest = (MockRequest) flowVariables.parseExpression(this.request);

            if (name != null) {
                String nameVal = (String) flowVariables.parseExpression(this.name);
                mockRequest.getHeaders().remove(nameVal);
            }
            
            if (matcher != null) {
                for (Object headerName : mockRequest.getHeaders().keySet()) {
                    if (headerName instanceof String) {
                        boolean matches = matcher.matches((String) headerName);
                        if (matches) {
                            mockRequest.getHeaders().remove(headerName);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
