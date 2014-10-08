package com.sabre.sabresonic.mockserver.core.util;

import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public final class ExpressionUtil {

    private ExpressionUtil() {
    }

    public static Object parseSpelExpression(final String expression, final Object context) {
        final ExpressionParser spelParser = new SpelExpressionParser();
        final EvaluationContext evaluationContext = new StandardEvaluationContext(context);
        return spelParser.parseExpression(expression).getValue(evaluationContext);
    }

    public static Object getValue(final String expression, final Object context) throws OgnlException {
        return Ognl.getValue(Ognl.parseExpression(expression), context);
    }
    
    public static void setValue(final String expression, final Object context, final Object value) throws OgnlException {
        Ognl.setValue(Ognl.parseExpression(expression), context, value);
    }
}
