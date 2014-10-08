package com.sabre.sabresonic.mockserver.frg.chain;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public abstract class AbstractHandler implements FrgHandler{

    private AbstractHandler successor;

    @Override
    public FrgResponse handleRequest(FrgRequest request) {

        FrgResponse result = proceed(request);
        if (result == null) {
            if (this.successor != null) {
                return this.successor.handleRequest(request);
            }
        }
        return result;
    }

    public abstract FrgResponse proceed(FrgRequest request);

    public void setSuccessor(AbstractHandler successor) {
        this.successor = successor;
    }

    public AbstractHandler getSuccessor() {
        return successor;
    }
}
