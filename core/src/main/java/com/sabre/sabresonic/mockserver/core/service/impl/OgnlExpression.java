package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import ognl.OgnlException;

/**
 * http://commons.apache.org/proper/commons-ognl/language-guide.html
 *
 * @author sg0218182
 */
public class OgnlExpression extends AbstractService {

    private String expression;

    public OgnlExpression() {
        super();
    }

    public OgnlExpression(final String expression) {
        this();
        this.expression = expression;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            flowVariables.parseExpression(expression);
        } catch (OgnlException ex) {
            throw new ServiceException(ex);
        }
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }
}
