package com.sabre.sabresonic.mockserver.frg.processor;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class DefaultFdgProcessorTest {
    
    /**
     * Test of process method, of class DefaultFdgProcessor.
     */
    @Test
    public void testProcess() {
        FrgRequest fdgRequest = new FrgRequest();
        fdgRequest.setRequest("Test data");
        fdgRequest.setResponse("Test data response");
        
        DefaultFdgProcessor instance = new DefaultFdgProcessor();

        FrgResponse result = instance.process(fdgRequest);
        assertEquals("Test data response", result.getResponse());
    }
    
    
    
}
