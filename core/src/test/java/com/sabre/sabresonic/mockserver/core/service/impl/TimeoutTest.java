/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class TimeoutTest {

    /**
     * Test of execute method, of class Timeout.
     */
    @Test
    public void testExecute() {
        String timeout = "10";

        FlowVariables flowVariables = new FlowVariables();
        Timeout instance = new Timeout();
        instance.setMilis(timeout);

        long start = System.currentTimeMillis();
        instance.execute(flowVariables);
        long duration = System.currentTimeMillis() - start;

        assertTrue(duration >= Long.parseLong(timeout));
    }

    @Test
    public void testExpression() {

            String timeout = "10";

            FlowVariables flowVariables = new FlowVariables();
            Timeout instance = new Timeout();
            instance.setMilis(timeout);





            // XPATH
            //         JXPathContext context = JXPathContext.newContext(instance);
            //         start = System.currentTimeMillis();
            //         Object value = context.getValue("timeout");
            //         Object value1 = context.getValue("timeout");
            //         Object value2 = context.getValue("timeout");
            //         duration = System.currentTimeMillis() - start;
            //         System.out.println(duration + " val: " + value + value1 + value2);




    }

}