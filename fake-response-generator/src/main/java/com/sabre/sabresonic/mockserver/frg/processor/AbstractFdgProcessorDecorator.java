package com.sabre.sabresonic.mockserver.frg.processor;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public abstract class AbstractFdgProcessorDecorator implements FdgProcessor{
    protected FdgProcessor fdgProcessor;

    public AbstractFdgProcessorDecorator(FdgProcessor fdgProcessor) {
        this.fdgProcessor = fdgProcessor;
    }

    public FdgProcessor getFdgProcessor() {
        return fdgProcessor;
    }

    public void setFdgProcessor(FdgProcessor fdgProcessor) {
        this.fdgProcessor = fdgProcessor;
    }

}
