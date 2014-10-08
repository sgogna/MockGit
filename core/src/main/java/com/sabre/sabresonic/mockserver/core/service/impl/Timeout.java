package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

public class Timeout extends AbstractService {

    private String milis = "1000";

    public Timeout() {
        super();
    }

    public Timeout(final String milis) {
        this();
        this.milis = milis;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            final String milisObj = String.valueOf(flowVariables.parseExpression(milis));
            final long sleepTime = Long.valueOf((String) milisObj);

            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    public void setMilis(final String milis) {
        this.milis = milis;
    }

    public String getMilis() {
        return milis;
    }
}
