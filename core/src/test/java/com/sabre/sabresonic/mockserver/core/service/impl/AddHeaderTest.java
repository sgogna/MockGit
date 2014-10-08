/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class AddHeaderTest {

    public FlowVariables getNewFlowVariables() {
        MockResponse response = new MockResponse();
        FlowVariables flowVariables = new FlowVariables();
        flowVariables.put("response", response);
        return flowVariables;
    }

    @Test
    public void testExecute() {
        FlowVariables flowVariables = getNewFlowVariables();
        AddHeader addHeaderService = new AddHeader("'Access-Control-Allow-Origin'", "'*'");
        addHeaderService.execute(flowVariables);

        MockResponse response = (MockResponse) flowVariables.get("response");

        assertTrue("*".equals(response.getHeaders().get("Access-Control-Allow-Origin")));
    }
}