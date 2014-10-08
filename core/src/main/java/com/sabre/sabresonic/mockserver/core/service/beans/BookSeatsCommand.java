package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * BookSeatsCommand
 */
public class BookSeatsCommand extends GenericHostCommand {
    public BookSeatsCommand(String commandStr) {
        super(commandStr);
    }

    @Override
    public String toString() {
        return "BookSeats";
    }
}
