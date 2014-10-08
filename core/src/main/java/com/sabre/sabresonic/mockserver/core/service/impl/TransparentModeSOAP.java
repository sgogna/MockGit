package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransparentModeSOAP extends AbstractService {

    private static final Logger LOG = LoggerFactory.getLogger(TransparentModeSOAP.class);

    public TransparentModeSOAP() {
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        LOG.info("The Mode Set is PASSTHRU Mode, nothing will be recorded on mock. All the Request will be going to down line systems!");
    }
}
