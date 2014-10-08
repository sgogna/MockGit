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
public class XPathInsertValue extends AbstractService {

    private String input;
    private String query;
    private String insert;

    public XPathInsertValue() {
        super();
    }

    public XPathInsertValue(final String input, final String query, final String insert) {
        this();
        this.input = input;
        this.query = query;
        this.insert = insert;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (input == null) {
                input = "request";
            }

            String xmlValue = transformToString(flowVariables.parseExpression(this.input));
            String insertValue = transformToString(flowVariables.parseExpression(this.insert));
            String xPathQueryValue = (String) flowVariables.parseExpression(this.query);

            String changedXml = XPathUtil.insertTextNode(xmlValue, xPathQueryValue, insertValue);

            setXml(flowVariables.parseExpression(this.input), changedXml);

        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    protected String transformToString(final Object obj) {
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

    protected void setXml(Object object, String xml) {
        if (object instanceof byte[]) {
            object = xml.getBytes();
        }
        if (object instanceof String) {
            object = xml;
        }
        if (object instanceof MockRequest) {
            ((MockRequest) object).setContent(xml.getBytes());
        }
        if (object instanceof MockResponse) {
            ((MockResponse) object).setContent(xml.getBytes());
        }
    }
}
