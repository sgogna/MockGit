package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * CancelSeatsCommand
 */
public class CancelSeatsCommand extends GenericHostCommand {
    public CancelSeatsCommand(String commandStr) {
        super(commandStr);
    }

    @Override
    public String toString() {
        return "CancelSeats";
    }
}
