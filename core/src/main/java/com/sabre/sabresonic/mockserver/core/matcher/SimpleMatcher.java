package com.sabre.sabresonic.mockserver.core.matcher;

import org.springframework.util.PatternMatchUtils;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class SimpleMatcher extends AbstractStringMatcher {

    public SimpleMatcher(final String pattern) {
        super(pattern);
    }

    @Override
    public boolean matches(final String toMatch) {
        return PatternMatchUtils.simpleMatch(getPattern(), toMatch);
    }

}
