package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * Created with IntelliJ IDEA.
 * User: SG0935379
 * Date: 12/19/12
 * Time: 10:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class TicketingBPCCommand extends GenericHostCommand
{
    public TicketingBPCCommand(String command)
    {
        super(command);
    }

    @Override
    public String toString() {
        return "TicketingBPC";
    }
}
