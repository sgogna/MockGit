package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * HostCommand
 */
public interface HostCommand {
    String getCommand();
    String sanitized();
    String getCommandPrefix();
    boolean isPassengerNameCommand();
    boolean isVICommand();
}