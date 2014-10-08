package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class ReplaceUrlPath extends AbstractService {

    private String request;
    private String pattern;
    private String value;

    public ReplaceUrlPath(String request, String pattern, String value) {
        this.request = request;
        this.pattern = pattern;
        this.value = value;
    }

    public ReplaceUrlPath(String pattern, String value) {
        this(null, pattern, value);
    }

    @Override
    public void execute(FlowVariables flowVariables) {
        super.execute(flowVariables);

        if (request == null) {
            request = "request";
        }
        if (value == null) {
            value = "''";
        }

        try {

            MockRequest mockRequest = (MockRequest) flowVariables.parseExpression(this.request);
            if (pattern != null) {
                String patternValue = (String) flowVariables.parseExpression(this.pattern);
                String toReplace = (String) flowVariables.parseExpression(this.value);

                URIBuilder uriBuilder = new URIBuilder(mockRequest.getURI());
                uriBuilder.setPath(uriBuilder.getPath().replaceAll(patternValue, toReplace));
                mockRequest.setURI(uriBuilder.build());
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
