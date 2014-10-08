package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * SecureFlightCommand
 */
public class SecureFlightCommand extends GenericHostCommand {
    public SecureFlightCommand(String commandStr) {
        super(commandStr);
    }

    @Override
    public String toString() {
        return "SecureFlight";
    }
}
