/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.executor.DefaultServiceExecutor;
import com.sabre.sabresonic.mockserver.core.factory.ServiceRegistryFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class HttpInvokeTest {
    
       protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            DefaultServiceExecutor executor = new DefaultServiceExecutor();
            executor.setServiceRegistry(ServiceRegistryFactory.createServiceRegistry());
            executor.execute(req, resp);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    
    @Test
    public void testServices() throws Exception {
//            HttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8084/testproxy/generic/replay/lukasz/SSW2010/VAVA/webqtrip.html?execution=e1s1");
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "http://localhost:8084/testproxy/generic/soap/record/SSW2010/VAVA/webqtrip.html?execution=e1s1");
            MockHttpServletResponse resp = new MockHttpServletResponse();

            process(req, resp);
    }
    
//    @Test
//    public void testExpression() throws IOException {
//        try {
//           // HttpGet request = new HttpGet("https://ctovm1823.dev.sabre.com:8442/SSW2010/VAVA/webqtrip.html?execution=e1s1");
//    //        HttpGet request = new HttpGet("http://localhost:8084/testproxy/generic/record/lukasz/webqtrip.html?execution=e1s1");
//            MockRequest request = MockRequest.create(new MockHttpServletRequest("GET", "https://ctovm1823.dev.sabre.com:8442/SSW2010/VAVA/webqtrip.html?execution=e1s1"));
//            
//            FlowVariables flowVariables = new FlowVariables();
//            flowVariables.put("request", request);
//            
//            InvokeHttp invokeHttp = new InvokeHttp("request", "response");
//            invokeHttp.execute(flowVariables);
//            MockResponse response = (MockResponse)flowVariables.get("response");
//            String content = IOUtils.toString(response.getContentBytes(), Charset.defaultCharset().name());
////            System.out.println( content);
//            assert content != null;
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(HttpInvokeTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }

}