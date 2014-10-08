package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.matcher.StringMatcher;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RemoveQueryParam extends AbstractService {

    private String request;
    private StringMatcher matcher;

    public RemoveQueryParam(final String request, final StringMatcher matcher) {
        this.request = request;
        this.matcher = matcher;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (request == null) {
                request = "request";
            }
            
            MockRequest mockRequest = (MockRequest)flowVariables.parseExpression(this.request);
            URI uri = convertToUri( mockRequest.getURI());

            URIBuilder uriBuilder = new URIBuilder(uri);

            List<NameValuePair> queryParams = uriBuilder.getQueryParams();
            uriBuilder.removeQuery();

            for (NameValuePair queryParam : queryParams) {
                boolean matches = matcher.matches(queryParam.getName());
                if (!matches) {
                    uriBuilder.addParameter(queryParam.getName(), queryParam.getValue());
                }
            }

            uri = uriBuilder.build();
            mockRequest.setURI(uri);

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
