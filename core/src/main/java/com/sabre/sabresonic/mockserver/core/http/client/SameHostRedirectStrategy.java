package com.sabre.sabresonic.mockserver.core.http.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.HttpContext;

/**
 * This implementation keeps original host
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class SameHostRedirectStrategy extends DefaultRedirectStrategy {

    @Override
    public URI getLocationURI(final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws ProtocolException {

        URI result = super.getLocationURI(request, response, context);
        if (this.isRedirected(request, response, context)) {
            try {
                if (request instanceof RequestWrapper) {
                    final RequestWrapper requestWrapper = (RequestWrapper) request;
                    if (requestWrapper.getOriginal() instanceof HttpUriRequest) {
                        final HttpUriRequest uriRequest = (HttpUriRequest) requestWrapper.getOriginal();
                        final HttpHost httpHost = URIUtils.extractHost(uriRequest.getURI());
                        result = URIUtils.rewriteURI(result, httpHost);
                    }
                }
            } catch (URISyntaxException ex) {
                throw new ProtocolException(ex.getMessage(), ex);
            }
        }
        return result;
    }
}
