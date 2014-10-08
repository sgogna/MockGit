/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class OgnlExpressionTest {

    FlowVariables flowVariables = new FlowVariables();
    OgnlExpression ognlExpression = new OgnlExpression();

    @Before
    public void setUp() {
        flowVariables = new FlowVariables();
    }

    @Test
    public void testSetFlowVariablesValue() {
        ognlExpression.setExpression("name = 'Lukasz' ");
        ognlExpression.execute(flowVariables);
        assertTrue("Lukasz".equals(flowVariables.get("name")));
    }

    @Test
    public void testOverwriteFlowVariablesValue() {
        flowVariables.put("name", "Tomasz");
        ognlExpression.setExpression("name = 'Lukasz'");
        ognlExpression.execute(flowVariables);
        assertTrue("Lukasz".equals(flowVariables.get("name")));
    }

    @Test
    public void testExpression() {
        int price = 100;
        int count = 3;
        
        flowVariables.put("price", price);
        flowVariables.put("count", count);
        ognlExpression.setExpression("result = price*count");
        ognlExpression.execute(flowVariables);
        assertTrue( (price*count) == (Integer)flowVariables.get("result"));
    }

}