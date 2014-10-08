package com.sabre.sabresonic.mockserver.frg.processor;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;

/**
 * Default processor which only rewrites input to output
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class DefaultFdgProcessor implements FdgProcessor {

    @Override
    public FrgResponse process(FrgRequest fdgRequest) {
        FrgResponse fdgResponse = new FrgResponse();
        fdgResponse.setResponse(fdgRequest.getResponse());
        return fdgResponse;
    }
}
