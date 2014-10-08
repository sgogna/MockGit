package com.sabre.sabresonic.mockserver.frg.generator;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public interface FrdRequestAware {
    void setRequest(FrgRequest request);
}
