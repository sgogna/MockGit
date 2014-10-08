package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class AddQueryParam extends AbstractService {

    private String request;
    private String name;
    private String value;

    public AddQueryParam(final String request, final String name, final String value) {
        this.request = request;
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (request == null) {
                request = "request";
            }
            
            MockRequest mockRequest = (MockRequest)flowVariables.parseExpression(this.request);
            String nameValue = (String)flowVariables.parseExpression(this.name);
            String valueValue = (String)flowVariables.parseExpression(this.value);
            
            if(mockRequest != null && nameValue != null){
                URI uri = convertToUri(mockRequest.getURI());
                URIBuilder uriBuilder = new URIBuilder(uri);
                uriBuilder.addParameter(nameValue, valueValue);
                uri = uriBuilder.build();
                mockRequest.setURI(uri);
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    protected URI convertToUri(Object urlObject) throws URISyntaxException {
        if (urlObject instanceof String) {
            return new URI((String) urlObject);
        }
        if (urlObject instanceof URI) {
            return (URI) urlObject;
        }
        return null;
    }
}
