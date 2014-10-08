package com.sabre.sabresonic.mockserver.core.service;

import com.sabre.sabresonic.mockserver.core.util.ExpressionUtil;
import java.util.HashMap;
import ognl.OgnlException;

public class FlowVariables extends HashMap<String, Object> {

    public Object parseExpression(final String expression) throws OgnlException {
        return ExpressionUtil.getValue(expression, this);
    }

    public void parseSetValueExpression(final String expression, final Object value) throws OgnlException {
        ExpressionUtil.setValue(expression, this, value);
    }

    public static FlowVariables createNew() {
        return new FlowVariables();
    }
}
