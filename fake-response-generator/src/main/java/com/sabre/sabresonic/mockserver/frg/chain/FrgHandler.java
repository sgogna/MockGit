package com.sabre.sabresonic.mockserver.frg.chain;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public interface FrgHandler {
        public FrgResponse handleRequest(FrgRequest request);
}
