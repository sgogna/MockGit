package com.sabre.sabresonic.mockserver.core.message.replacers;

/**
 * DataReplacer
 */
public interface DataReplacer {
    String replace(String sessionId, String originalResponse);
}
