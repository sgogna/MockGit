package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class AddHeader extends AbstractService {

    private String response;
    private String name;
    private String value;

    public AddHeader() {
        super();
    }

    public AddHeader(final String nam, final String valu) {
        this();
        this.name = nam;
        this.value = valu;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (response == null) {
                response = "response";
            }
            if (name != null) {
                final String headerName = String.valueOf(flowVariables.parseExpression(this.name));
                final String headerValue = String.valueOf(flowVariables.parseExpression(this.value));

                MockResponse mockResponse = (MockResponse) flowVariables.parseExpression(this.response);

                mockResponse.getHeaders().put(headerName, headerValue);
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
