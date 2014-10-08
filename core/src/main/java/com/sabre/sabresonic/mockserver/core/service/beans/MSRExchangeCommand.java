package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * MSRExchangeCommand
 */
public class MSRExchangeCommand extends GenericHostCommand {
    public MSRExchangeCommand(String commandStr) {
        super(commandStr);
    }

    @Override
    public String toString() {
        return "MSRExchange";
    }
}
