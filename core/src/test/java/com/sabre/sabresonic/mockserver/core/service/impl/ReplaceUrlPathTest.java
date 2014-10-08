/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class ReplaceUrlPathTest {

    public FlowVariables getNewFlowVariables(String url) throws URISyntaxException, UnsupportedEncodingException {
        MockRequest request = MockRequest.create(new MockHttpServletRequest("GET", url));
        FlowVariables flowVariables = new FlowVariables();
        flowVariables.put("request", request);
        return flowVariables;
    }

    public String execute(final String targetUrl, String pattern, String value) throws URISyntaxException, UnsupportedEncodingException {
        FlowVariables flowVariables = getNewFlowVariables(targetUrl);
        ReplaceUrlPath instance = new ReplaceUrlPath(pattern, value);
        instance.execute(flowVariables);

        MockRequest mockRequest = (MockRequest) flowVariables.get("request");
        return mockRequest.getURI().toString();
    }

    /**
     * Test of execute method, of class ReplaceUrlPath.
     */
    @Test
    public void testExecute() throws URISyntaxException, UnsupportedEncodingException {

        final String targetUrl = "http://localhost:8084/testproxy/generic/replay/lukasz/SSW2010/VAVA/webqtrip.html";


        assertEquals("http://localhost:8084/webqtrip.html", execute(targetUrl, "'testproxy/generic/replay/lukasz/SSW2010/VAVA/'", null));
        assertEquals("http://localhost:8084/webqtrip.html", execute(targetUrl, "'testproxy/generic/replay/lukasz/SSW2010/VAVA/'", "''"));
        assertEquals("http://localhost:8084/test/webqtrip.html", execute(targetUrl, "'testproxy/generic/replay/lukasz/SSW2010/VAVA/'", "'test/'"));

    }
}