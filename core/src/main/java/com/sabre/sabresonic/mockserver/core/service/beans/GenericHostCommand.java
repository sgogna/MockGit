package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * GenericHostCommand
 */
class GenericHostCommand implements HostCommand {
    private String command;

    public GenericHostCommand(String command) {
        this.command = command;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String sanitized() {
        if ( command == null ) {
            return null;
        }
        return command.replaceAll("\\s","").replaceAll("/", "");
    }

    @Override
    public String getCommandPrefix() {
        throw new UnsupportedOperationException("Generic command: unable to determine command prefix");
    }

    public boolean isPassengerNameCommand() {
        return command != null && command.startsWith("-1");
    }

    public boolean isVICommand() {
        return command != null && command.startsWith("*VI");
    }

    @Override
    public String toString() {
        return sanitized();
    }
}

