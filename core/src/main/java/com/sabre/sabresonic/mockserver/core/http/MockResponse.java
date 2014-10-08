package com.sabre.sabresonic.mockserver.core.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class MockResponse {

    private static final Logger LOG = LoggerFactory.getLogger(MockResponse.class);
    private Locale locale;
    private int statusCode;
    private Map<String, String> headers = new HashMap();
    private byte[] content;

    public MockResponse() {
    }

    public MockResponse(final HttpResponse httpResponse) {
        this.locale = httpResponse.getLocale();
        this.statusCode = httpResponse.getStatusLine().getStatusCode();

        for (Header header : httpResponse.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        try {
            content = EntityUtils.toByteArray(httpResponse.getEntity());
        } catch (Exception ex) {
            LOG.warn(null, ex);
        }
    }

    public void fill(final HttpServletResponse servletResponse)
            throws IOException {
        for (String key : headers.keySet()) {
            servletResponse.addHeader(key, headers.get(key));
        }

        if (statusCode == 0) {
            statusCode = HttpStatus.SC_OK;
        }
        servletResponse.setStatus(statusCode);

        if (locale == null) {
            locale = Locale.getDefault();
        }
        servletResponse.setLocale(locale);
        servletResponse.setContentLength(content.length);
        IOUtils.write(content, servletResponse.getOutputStream());
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(final Map headers) {
        this.headers = headers;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    public void setContent(final byte[] contentBytes) {
        this.content = Arrays.copyOf(contentBytes, contentBytes.length);
    }
}
