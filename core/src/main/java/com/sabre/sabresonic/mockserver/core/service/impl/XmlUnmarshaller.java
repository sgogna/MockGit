package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.XstreamUtils;
import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class XmlUnmarshaller extends AbstractService {

    private String xml;
    private String result;

    public XmlUnmarshaller(final String xml, final String result) {
        this.xml = xml;
        this.result = result;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            Object xmlObj = flowVariables.parseExpression(xml);

            Object resultObj = null;
            if (xmlObj instanceof String) {
                resultObj = XstreamUtils.getXstream().fromXML((String) xmlObj);
            }
            if (xmlObj instanceof File) {
                resultObj = XstreamUtils.getXstream().fromXML((File) xmlObj);
            }
            if (xmlObj instanceof InputStream) {
                resultObj = XstreamUtils.getXstream().fromXML((InputStream) xmlObj);
            }
            if (xmlObj instanceof Reader) {
                resultObj = XstreamUtils.getXstream().fromXML((Reader) xmlObj);
            }
            if (xmlObj instanceof URL) {
                resultObj = XstreamUtils.getXstream().fromXML((URL) xmlObj);
            }
            flowVariables.parseSetValueExpression(result, resultObj);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
