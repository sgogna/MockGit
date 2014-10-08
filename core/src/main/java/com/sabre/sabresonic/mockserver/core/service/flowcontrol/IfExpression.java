package com.sabre.sabresonic.mockserver.core.service.flowcontrol;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.service.Service;
import ognl.OgnlException;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class IfExpression extends AbstractService {

    private String expression;
    private Service then;
    private Service otherwise;

    public IfExpression() {
        super();
    }

    public IfExpression(final String expression, final Service then, final Service otherwise) {
        super();
        this.expression = expression;
        this.then = then;
        this.otherwise = otherwise;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        if (isTrue(flowVariables)) {
            executeThen(flowVariables);
        } else {
            executeOtherwise(flowVariables);
        }
    }

    private Boolean isTrue(final FlowVariables flowVariables) {
        try {
            Boolean conditionResult = (Boolean) flowVariables.parseExpression(expression);
            return conditionResult != null && conditionResult;
        } catch (OgnlException ex) {
            throw new ServiceException(ex);
        }
    }

    private void executeThen(final FlowVariables flowVariables) {
        if (then != null) {
            then.execute(flowVariables);
        }
    }

    private void executeOtherwise(final FlowVariables flowVariables) {
        if (otherwise != null) {
            otherwise.execute(flowVariables);
        }
    }

    public Service getThen() {
        return then;
    }

    public void setThen(final Service then) {
        this.then = then;
    }

    public Service getOtherwise() {
        return otherwise;
    }

    public void setOtherwise(final Service otherwise) {
        this.otherwise = otherwise;
    }
}
