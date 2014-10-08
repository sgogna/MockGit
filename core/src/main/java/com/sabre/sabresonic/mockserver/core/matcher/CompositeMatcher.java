package com.sabre.sabresonic.mockserver.core.matcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class CompositeMatcher extends AbstractStringMatcher {

    private final List<StringMatcher> matchers = new ArrayList();

    @Override
    public boolean matches(final String toMatch) {
        for (StringMatcher matcher : matchers) {
            if (matcher.matches(toMatch)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(final StringMatcher matcher) {
        return matchers.add(matcher);
    }
}
