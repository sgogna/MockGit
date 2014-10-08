package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.http.client.SameHostRedirectStrategy;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.SystemDefaultHttpClient;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class InvokeHttp extends AbstractService {

    private String requestRef;
    private String responseRef;
    private String endpoint;

    public InvokeHttp(final String requestRef, final String responseRef) {
        this.requestRef = requestRef;
        this.responseRef = responseRef;
    }

    public InvokeHttp(final String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);

        if (requestRef == null) {
            requestRef = "request";
        }
        if (responseRef == null) {
            responseRef = "response";
        }

        HttpClient httpclient = null;
        try {
            httpclient = wrapClient(new SystemDefaultHttpClient());
            Validate.notNull(requestRef);

            MockRequest mockRequest = (MockRequest) flowVariables.parseExpression(requestRef);

            if (endpoint != null) {
                // change URI
                String endpointValue = (String) flowVariables.parseExpression(endpoint);
                URI chnagedURI = changeEndpoint(mockRequest.getURI(), endpointValue);
                mockRequest.setURI(chnagedURI);
            }

            getLogger().debug("executing request " + mockRequest.getURI());
            HttpUriRequest apacheRequest = createHttpUriRequest(mockRequest);
            HttpResponse apacheResponse = httpclient.execute(apacheRequest);
            if (responseRef != null) {
                flowVariables.parseSetValueExpression(responseRef, new MockResponse(apacheResponse));
            }

        } catch (Exception ex) {
            throw new ServiceException(ex);
        } finally {
            if (httpclient != null) {
                httpclient.getConnectionManager().shutdown();
            }
        }
    }

    public static HttpClient wrapClient(HttpClient base) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        TrustStrategy trustStrategy = new TrustSelfSignedStrategy();
        SSLSocketFactory ssf = new SSLSocketFactory(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = base.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
        sr.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        DefaultHttpClient httpClient = new DefaultHttpClient(ccm, base.getParams());
        httpClient.setRedirectStrategy(new SameHostRedirectStrategy());
        return httpClient;
    }

    public HttpUriRequest createHttpUriRequest(MockRequest mockRequest) {

        String method = mockRequest.getMethod();
        URI uri = mockRequest.getURI();
//        HttpMethod.
        HttpUriRequest apacheRequest = null;
        if ("get".equalsIgnoreCase(method)) {
            apacheRequest = new HttpGet(uri);
        }
        if ("post".equalsIgnoreCase(method)) {
            apacheRequest = new HttpPost(uri);
        }
        if ("head".equalsIgnoreCase(method)) {
            apacheRequest = new HttpHead(uri);
        }
        if ("options".equalsIgnoreCase(method)) {
            apacheRequest = new HttpOptions(uri);
        }
        if ("put".equalsIgnoreCase(method)) {
            apacheRequest = new HttpPut(uri);
        }
        if ("delete".equalsIgnoreCase(method)) {
            apacheRequest = new HttpDelete(uri);
        }
        if ("trace".equalsIgnoreCase(method)) {
            apacheRequest = new HttpTrace(uri);
        }
        if (apacheRequest != null) {
            for (String paramName : mockRequest.getParams().keySet()) {
                apacheRequest.getParams().setParameter(paramName.toString(), mockRequest.getParams().get(paramName));
            }
            for (String headerName : mockRequest.getHeaders().keySet()) {
                String headerValue = mockRequest.getHeaders().get(headerName);
                if (!"location".equals(headerName)) {
                    apacheRequest.addHeader(headerName, headerValue);
                }
            }
            if (apacheRequest instanceof HttpEntityEnclosingRequest) {

                HttpEntity entity = new ByteArrayEntity(mockRequest.getContent());
                HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) apacheRequest;
                entityRequest.setEntity(entity);
            }
        }



        return apacheRequest;
    }

    protected URI changeEndpoint(URI actual, String endpoint) throws URISyntaxException {
        URI targetURI = new URI(endpoint);

        URIBuilder uriBuilder = new URIBuilder(actual);
        uriBuilder.setScheme(targetURI.getScheme());
        uriBuilder.setHost(targetURI.getHost());
        uriBuilder.setPort(targetURI.getPort());
        uriBuilder.setPath(targetURI.getPath() + "/" + uriBuilder.getPath());
        return uriBuilder.build();
    }
}
