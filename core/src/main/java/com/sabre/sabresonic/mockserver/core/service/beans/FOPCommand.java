package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * FOPCommand
 */
public class FOPCommand extends GenericHostCommand {
    public FOPCommand(String commandStr) {
        super(commandStr);
    }

    @Override
    public String toString() {
        return "FOPCommand";
    }
}
