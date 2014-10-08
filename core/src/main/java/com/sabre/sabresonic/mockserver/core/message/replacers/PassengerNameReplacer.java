package com.sabre.sabresonic.mockserver.core.message.replacers;

/**
 * PassengerNameReplacer
 */
public interface PassengerNameReplacer {
    String replace(String originalResponse, PassengerNameReplacer newPassengerName);
}
