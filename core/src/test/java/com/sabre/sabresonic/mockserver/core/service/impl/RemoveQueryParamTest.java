package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.matcher.SimpleMatcher;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RemoveQueryParamTest {

    /**
     * Test of execute method, of class RemoveQueryParam.
     */
//    @Test
//    public void testExecute() throws URISyntaxException {
//        URI source = new URI("https://www.google.pl/search?q=uri+query+builder&ie=UTF-8");
//        URI changedUri = changeUri(source, "q");
//        assertTrue(new URI("https://www.google.pl/search?ie=UTF-8"), changedUri);
//    }
//    
    @Test
    public void testRemoveQueryParamFromRequest() throws URISyntaxException, UnsupportedEncodingException {
        FlowVariables flowVariables = new FlowVariables();
        flowVariables.put("request", MockRequest.create(new MockHttpServletRequest("GET", "https://www.google.pl/search?q=uri+query+builder&ie=UTF-8")));
        RemoveQueryParam instance = new RemoveQueryParam(null, new SimpleMatcher("ie"));
        instance.execute(flowVariables);
        
        assertTrue(new URI("https://www.google.pl/search?q=uri+query+builder"), ((MockRequest)flowVariables.get("request")).getURI());
    }
    
    
//    public URI changeUri(URI uri, String pattern){
//        FlowVariables flowVariables = new FlowVariables();
//        flowVariables.put("uri", uri);
//        RemoveQueryParam instance = new RemoveQueryParam("uri", new SimpleMatcher(pattern));
//        instance.execute(flowVariables);
//        return (URI)flowVariables.get("uri");
//    }
    
    public void assertTrue(URI expected, URI actual){
        Assert.assertTrue(expected.compareTo(actual) == 0);
    }
    
}