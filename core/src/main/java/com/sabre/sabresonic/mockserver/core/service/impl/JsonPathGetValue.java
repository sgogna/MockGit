package com.sabre.sabresonic.mockserver.core.service.impl;

import com.jayway.jsonpath.JsonPath;
import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class JsonPathGetValue extends AbstractService {

    private String input;
    private String query;
    private String result;

    public JsonPathGetValue() {
        super();
    }

    public JsonPathGetValue(final String input, final String query, final String result) {
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

            String jsonValue = transformToString(flowVariables.parseExpression(this.input));
            String jsonPathValue = (String) flowVariables.parseExpression(this.query);
            
            Object resultObject = JsonPath.read(jsonValue, jsonPathValue);

            if (result != null) {
                flowVariables.parseSetValueExpression(result, resultObject);
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
