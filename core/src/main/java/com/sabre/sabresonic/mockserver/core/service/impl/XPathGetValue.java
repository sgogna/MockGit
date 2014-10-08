package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.util.XPathUtil;
import com.sabre.sabresonic.mockserver.core.util.xpath.MockNode;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NodeList;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class XPathGetValue extends AbstractService {

    private String input;
    private String query;
    private String result;

    public XPathGetValue() {
        super();
    }

    public XPathGetValue(final String input, final String query, final String result) {
        this();
        this.input = input;
        this.query = query;
        this.result = result;
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

            NodeList nodeList = XPathUtil.getNodes(xmlValue, xPathQueryValue);

            List nodes = new ArrayList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                nodes.add(new MockNode(nodeList.item(i)));
            }

            if (result != null) {
                flowVariables.parseSetValueExpression(result, nodes);
            }
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
}
