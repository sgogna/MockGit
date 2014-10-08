package com.sabre.sabresonic.mockserver.core.matcher;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RegexMatcher extends AbstractStringMatcher {

    public RegexMatcher(final String pattern) {
        super(pattern);
    }

    @Override
    public boolean matches(final String toMatch) {
        return toMatch.matches(getPattern());
    }

}
