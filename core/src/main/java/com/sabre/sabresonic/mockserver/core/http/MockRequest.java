package com.sabre.sabresonic.mockserver.core.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import com.sabre.sabresonic.mockserver.core.message.datagrabbers.DataGrabber;
import com.sabre.sabresonic.mockserver.core.service.beans.HostCommand;
import com.sabre.sabresonic.mockserver.core.service.beans.HostCommandFactory;
import com.sabre.sabresonic.mockserver.core.util.XPathUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public final class MockRequest {

    private static final Logger LOG = LoggerFactory.getLogger(MockRequest.class);
    private URI URI;
    private String method;
    private String query;
    private Map<String, Object> params = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap();
    private String contextPath;
    private String servletPath;
    private String sessionId;
    private String soapAction;
    private String conversationId;
    private HostCommand hostCommand;
    private String requestString;
    private byte[] content;
    private MockRequest() {
    }

    public static MockRequest create(
            final HttpServletRequest servletRequest)
            throws URISyntaxException, UnsupportedEncodingException {

        final MockRequest mockRequest = new MockRequest();
        mockRequest.setMethod(servletRequest.getMethod());
        mockRequest.setContextPath(servletRequest.getContextPath());
        mockRequest.setServletPath(servletRequest.getServletPath());
        mockRequest.setURI(extractUri(servletRequest));
        mockRequest.setParams(extractParams(servletRequest));
        mockRequest.setHeaders(extractHeaders(servletRequest));
        mockRequest.setContent(extractPayload(servletRequest));
        mockRequest.setSoapAction(new String(extractPayload(servletRequest), "UTF-8"));
        mockRequest.setConversationId(new String(extractPayload(servletRequest), "UTF-8"));
        return mockRequest;
    }

    private static URI extractUri(
            final HttpServletRequest servletRequest)
            throws URISyntaxException {
        final String orginalUri = servletRequest.getRequestURI();
        final URIBuilder uriBuilder = new URIBuilder(orginalUri);
        if (servletRequest.getQueryString() != null) {
            uriBuilder.setQuery(servletRequest.getQueryString());
        }
        return uriBuilder.build();
    }

    private static Map<String, Object> extractParams(
            final HttpServletRequest servletRequest) {

        final Map<String, Object> params = new HashMap();
        for (Object entry : servletRequest.getParameterMap().entrySet()) {
            final Entry keyValue = (Entry) entry;
            params.put(keyValue.getKey().toString(), keyValue.getValue());
        }
        return params;
    }

    private static Map<String, String> extractHeaders(
            final HttpServletRequest servletRequest) {

        final Map<String, String> headers = new HashMap();
        for (final Enumeration en = servletRequest.getHeaderNames(); en.hasMoreElements();) {
            final String headerName = (String) en.nextElement();
            if (!"location".equals(headerName)) {
                final String headerValue = servletRequest.getHeader(headerName);
                headers.put(headerName, headerValue);
            }
        }
        return headers;
    }

    private static byte[] extractPayload(
            final HttpServletRequest servletRequest) {
        byte[] payload = {};
        try {
            if (servletRequest.getReader() != null) {
                payload = IOUtils.toByteArray(servletRequest.getReader());
            }
        } catch (IOException ex) {
            LOG.warn(null, ex);
        }
        return payload;
    }

    public URI getURI() {
        return URI;
    }

    public void setURI(URI uri) {
        this.URI = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(final Map<String, Object> parameters) {
        this.params = parameters;
    }

    public void putParam(final String key, final Object value) {
        this.params.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(final Map<String, String> header) {
        this.headers = header;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(final String path) {
        this.contextPath = path;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(final String path) {
        this.servletPath = path;
    }

    public void setContent(final byte[] contentBytes) {
        this.content = Arrays.copyOf(contentBytes, contentBytes.length);
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }


    public void setSoapAction(final String mockRequest)
    {
        String s = new String(mockRequest);
        int start = s.indexOf(":Action>");
        if (start < 0)
        {
            soapAction = getActionNameAfterBodyTag(s);
        }
        else
        {
            start += 8;
            int end = s.indexOf("<", start) - 2;
            this.soapAction =  s.substring(start, end);
        }
    }

    public String getSoapAction()
    {
        return soapAction;
    }

    private String getActionNameAfterBodyTag(String s)
    {
        int body = s.indexOf(":Body>");
        if (body > 0)
        {
            int endOfBody = body + 6;
            int maxCharOfActionName = 100;
            int canDidateActionNameEndIndex = endOfBody + maxCharOfActionName;
            String candidateActionName = s.substring(endOfBody, canDidateActionNameEndIndex).trim();
            Pattern pattern = Pattern.compile("[\\S]+");
            Matcher matcher = pattern.matcher(candidateActionName.replaceAll("<", ""));
            if (matcher.find())
            {
                String group = matcher.group();
                if (group.contains(":"))
                {
                    return group.substring(group.indexOf(":") + 1, group.length() - 2);
                }
                return group.substring(0, group.length() - 2);
            }
        }

        return null;
    }

    public void setConversationId(String mockRequest)
    {
        if (sessionId == null)
        {
            String rqnow = new String(mockRequest);
            String SessionIDStart_STR = ":ConversationId>";
            String SessionIDEnd_STR = "</";
            int SessionIDStart_INT;
            int SessionIDEnd_INT;
            String currentSessionID = "";

            SessionIDStart_INT = rqnow.indexOf(SessionIDStart_STR);
            if (SessionIDStart_INT > 0)
            {
                SessionIDEnd_INT = rqnow.indexOf(SessionIDEnd_STR, SessionIDStart_INT);
                sessionId = rqnow.substring(SessionIDStart_INT + 16, SessionIDEnd_INT);
                System.out.println("\n this is what comes from request ::::::  getConversationId=" + currentSessionID + "<--");
            }
        }
        this.conversationId = sessionId;
    }

    public String getConversationId()
    {
       return conversationId;
    }

    public HostCommand getHostCommand()
    {
        if (hostCommand == null)
        {
            String requestContent = XPathUtil.prettyFormat(getRequestString(), 2);
            hostCommand = HostCommandFactory.create(requestContent);
        }
        return hostCommand;
    }

    public String getRequestString()
    {
        if (requestString == null)
        {
            requestString = new String(getContent());
        }
        return requestString;
    }
}
