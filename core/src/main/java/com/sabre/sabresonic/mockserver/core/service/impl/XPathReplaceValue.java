package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.util.XPathUtil;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class XPathReplaceValue extends AbstractService {

    private String input;
    private String query;
    private String newValue;

    public XPathReplaceValue() {
        super();
    }

    public XPathReplaceValue(String input, String query, String newValue) {
        this();
        this.input = input;
        this.query = query;
        this.newValue = newValue;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (input == null) {
                input = "request";
            }

            String xmlValue = transformToString(flowVariables.parseExpression(this.input));
            String xPathQueryValue = (String) flowVariables.parseExpression(this.query);
            String xPathNewValue = (String) flowVariables.parseExpression(this.newValue);

            String chnagedXml = XPathUtil.replaceNodeValue(xmlValue, xPathQueryValue, xPathNewValue);

            setXml(flowVariables.parseExpression(this.input), chnagedXml);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    protected String transformToString(Object obj) {
        if (obj instanceof byte[]) {
            return new String((byte[]) obj);
        }
        if (obj instanceof MockRequest) {
            return new String(((MockRequest) obj).getContent());
        }
        if (obj instanceof MockResponse) {
            return new String(((MockResponse) obj).getContent());
        }

        return String.valueOf(obj);
    }

    protected void setXml(Object obj, String xml) {
        if (obj instanceof byte[]) {
            obj = xml.getBytes();
        }
        if (obj instanceof String) {
            obj = xml;
        }
        if (obj instanceof MockRequest) {
            ((MockRequest) obj).setContent(xml.getBytes());
        }
        if (obj instanceof MockResponse) {
            ((MockResponse) obj).setContent(xml.getBytes());
        }
    }
}
