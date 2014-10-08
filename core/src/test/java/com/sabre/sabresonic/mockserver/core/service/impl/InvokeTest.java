package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.generator.UriBasedFilenameGeneratorTest;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class InvokeTest {
        private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(InvokeTest.class);

    public InvokeTest() {
    }

    /**
     * Test of execute method, of class InvokeService.
     */
    @Test
    public void testExecute() {
        LOG.info("execute");
        FlowVariables flowVariables = FlowVariables.createNew();
        flowVariables.put("name", "Lukasz");
        flowVariables.put("surname", "Kwiatkowski");

        Invoke invokeService = new Invoke();
        invokeService.addInput("'imie'", "name");
        invokeService.addOutput("'mojeImie'", "imie");

        invokeService.execute(flowVariables);
        
        LOG.info(flowVariables.toString());
    }
}