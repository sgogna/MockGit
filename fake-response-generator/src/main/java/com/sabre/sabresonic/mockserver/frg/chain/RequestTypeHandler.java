package com.sabre.sabresonic.mockserver.frg.chain;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;
import com.sabre.sabresonic.mockserver.frg.processor.FdgProcessor;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RequestTypeHandler extends AbstractHandler {

    protected String regex = "^$";
    protected FdgProcessor processor;

    @Override
    public FrgResponse proceed(FrgRequest fdgRequest) {
        String requestType = fdgRequest.getRequestType();
        if (requestType != null && requestType.matches(regex)) {
                return processor.process(fdgRequest);
        }
        return null;
    }

    public FdgProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(FdgProcessor processor) {
        this.processor = processor;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }


}
