package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Spring Expression Language (SpEL)
 * http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/expressions.html
 *
 * @author sg0218182
 */
public class SpelExpression extends AbstractService {

    private String expression;
    private ExpressionParser spelParser = new SpelExpressionParser();

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        if (expression != null) {
            Expression parsedExp = spelParser.parseExpression(expression);
            EvaluationContext context = new StandardEvaluationContext(flowVariables);
            parsedExp.getValue(context);
        }
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }
}
