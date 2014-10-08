package com.sabre.sabresonic.mockserver.frg.processor;

import com.sabre.sabresonic.mockserver.frg.generator.FrdRequestAware;
import com.sabre.sabresonic.mockserver.frg.generator.Generator;
import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;

/**
 * Overwrites response if regex condition is true
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RegexOvervriteProcessor extends AbstractFdgProcessorDecorator {

    protected Generator generator;
    protected String regex = "^$";
    protected Boolean processNullResponse = Boolean.TRUE;

    public RegexOvervriteProcessor(FdgProcessor fdgProcessor) {
        super(fdgProcessor);
    }

    @Override
    public FrgResponse process(FrgRequest fdgRequest) {
        FrgResponse fdgResponse = this.fdgProcessor.process(fdgRequest);
        if (this.generator instanceof FrdRequestAware) {
            ((FrdRequestAware) this.generator).setRequest(fdgRequest);
        }
        if (fdgResponse.getResponse() == null) {
            if (getProcessNullResponse()) {
                fdgResponse.setResponse(this.generator.process());
            }
        } else {
            if (fdgResponse.getResponse().matches(getRegex())) {
                fdgResponse.setResponse(this.generator.process());
            }
        }

        return fdgResponse;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public Generator getGenerator() {
        return generator;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Boolean getProcessNullResponse() {
        return processNullResponse;
    }

    public void setProcessNullResponse(Boolean processNullResponse) {
        this.processNullResponse = processNullResponse;
    }
}
