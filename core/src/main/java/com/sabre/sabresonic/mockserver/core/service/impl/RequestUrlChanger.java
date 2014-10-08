package com.sabre.sabresonic.mockserver.core.service.impl;

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
public class RequestUrlChanger extends AbstractService {

    private String requestRef;
    private String basePathRef;
    private String targetUrl;

    public RequestUrlChanger(String requestRef, String targetUrl, String basePathRef) {
        this.requestRef = requestRef;
        this.targetUrl = targetUrl;
        this.basePathRef = basePathRef;
    }

    public RequestUrlChanger(String requestRef, String targetUrl) {
        this.requestRef = requestRef;
        this.targetUrl = targetUrl;
    }

    @Override
    public void execute(FlowVariables flowVariables) {
        super.execute(flowVariables);
        
        if(requestRef == null){
            requestRef = "request";
        }
        try {

            MockRequest request = (MockRequest) flowVariables.parseExpression(this.requestRef);
            URI targetUri = convertToUri(flowVariables.parseExpression(this.targetUrl));
            String sourceBasePath = null;
            if (this.basePathRef != null) {
                sourceBasePath = (String) flowVariables.parseExpression(this.basePathRef);
            }

            URIBuilder uriBuilder = new URIBuilder(request.getURI().toString());
            uriBuilder.setScheme(targetUri.getScheme());
            uriBuilder.setHost(targetUri.getHost());
            uriBuilder.setPort(targetUri.getPort());
            if (sourceBasePath != null) {
                uriBuilder.setPath(uriBuilder.getPath().replaceAll(sourceBasePath, targetUri.getPath()));
            } else {
                uriBuilder.setPath(targetUri.getPath());
            }
            request.setURI(uriBuilder.build());
        } catch (Exception ex) {
            getLogger().error(null, ex);
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
