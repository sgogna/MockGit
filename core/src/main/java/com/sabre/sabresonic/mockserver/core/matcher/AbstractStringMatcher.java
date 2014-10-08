package com.sabre.sabresonic.mockserver.core.matcher;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public abstract class AbstractStringMatcher implements StringMatcher {

    private String pattern;

    public AbstractStringMatcher() {
    }

    public AbstractStringMatcher(final String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
