package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * FOPEntryCommand
 */
public class FOPEntryCommand extends GenericHostCommand {
    public FOPEntryCommand(String command) {
        super(command);
    }

    @Override
    public String toString() {
        return "FOPEntryCommand";
    }
}
