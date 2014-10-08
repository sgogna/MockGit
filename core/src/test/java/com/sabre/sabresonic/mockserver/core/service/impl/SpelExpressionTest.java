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
public class SpelExpressionTest {
    
    FlowVariables flowVariables = new FlowVariables();
    SpelExpression spelExpression = new SpelExpression();
    
    @Before
    public void setUp() {
        flowVariables = new FlowVariables();
    }

    @Test
    public void testSetFlowVariablesValue() {
        spelExpression.setExpression("['name']='Lukasz'");
        spelExpression.execute(flowVariables);
        assertTrue("Lukasz".equals(flowVariables.get("name")));
    }
    
    @Test
    public void testOverwriteFlowVariablesValue() {
        flowVariables.put("name", "Tomasz");
        spelExpression.setExpression("['name']='Lukasz'");
        spelExpression.execute(flowVariables);
        assertTrue("Lukasz".equals(flowVariables.get("name")));
    }
    
    @Test
    public void testExpression() {
        int price = 100;
        int count = 3;
        
        flowVariables.put("price", price);
        flowVariables.put("count", count);
        spelExpression.setExpression("['result']=['price']*['count']");
        spelExpression.execute(flowVariables);
        assertTrue( (price*count) == (Integer)flowVariables.get("result"));
    }

}