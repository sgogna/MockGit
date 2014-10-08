package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.XstreamUtils;
import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

public class XmlMarshaller extends AbstractService {

    private String object;
    private String result;

    public XmlMarshaller(final String object, final String result) {
        super();
        this.object = object;
        this.result = result;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        try {
            super.execute(flowVariables);
            final Object toMarshal = flowVariables.parseExpression(object);
            final String xml = XstreamUtils.getXstream().toXML(toMarshal);
            flowVariables.parseSetValueExpression(result, xml);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
