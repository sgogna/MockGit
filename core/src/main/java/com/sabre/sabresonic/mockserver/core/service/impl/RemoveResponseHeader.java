package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.matcher.StringMatcher;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RemoveResponseHeader extends AbstractService {

    private String response;
    private String name;
    private StringMatcher matcher;

    public RemoveResponseHeader() {
        super();
    }

    public RemoveResponseHeader(String response) {
        this();
        this.response = response;
    }

    public RemoveResponseHeader(String name, StringMatcher matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    public void execute(FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (response == null) {
                response = "response";
            }
            MockResponse mockResponse = (MockResponse) flowVariables.parseExpression(this.response);

            if (name != null) {
                String nameVal = (String) flowVariables.parseExpression(this.name);
                mockResponse.getHeaders().remove(nameVal);
            }
            
            if (matcher != null) {
                for (Object headerName : mockResponse.getHeaders().keySet()) {
                    if (headerName instanceof String) {
                        boolean matches = matcher.matches((String) headerName);
                        if (matches) {
                            mockResponse.getHeaders().remove(headerName);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
