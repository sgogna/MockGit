package com.sabre.sabresonic.mockserver.frg.processor;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;

/**
 * Interface to process FdgRequest
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public interface FdgProcessor {
    
    /**
     * Process FdgRequest
     * @param fdgRequest FdgRequest
     * @return FdgResponse
     */
    FrgResponse process(FrgRequest fdgRequest);
}
