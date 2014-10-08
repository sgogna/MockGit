package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * DisplaySeatMapCommand
 */
public class DisplaySeatMapCommand extends GenericHostCommand {
    public DisplaySeatMapCommand(String commandStr) {
        super(commandStr);
    }

    @Override
    public String toString() {
        return "DisplaySeatMapCommand";
    }
}
