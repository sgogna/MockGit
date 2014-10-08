package com.sabre.sabresonic.mockserver.core.service.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PassengerNameCommand
 */
public class PassengerNameCommand extends GenericHostCommand {
    Pattern pattern = Pattern.compile("-[1]*([A-Z]*)/([A-Z]*)\\s(MR|MRS|MS|MISS)*(?:\\*)*(ADT|CHD|CNN)*.*");
    private PassengerName passengerName;

    // -1YXJNGTJFXF/YXJNGTJFXF MR*ADTÂ§
    public PassengerNameCommand(String command) {
        super(command);
        Matcher matcher = pattern.matcher(command.toUpperCase());
        if (matcher.matches()) {
            this.passengerName = new PassengerName(matcher.group(1), matcher.group(2));
            if ( matcher.groupCount() > 2 ) {
                this.passengerName.title = matcher.group(3);
            }
            if ( matcher.groupCount() > 3 ) {
                this.passengerName.type = matcher.group(4);
            }
        }
    }

    public PassengerName getPassengerName() {
        return passengerName;
    }

    @Override
    public String getCommandPrefix() {
        return "-1";
    }

    @Override
    public String toString() {
        return PassengerNameCommand.class.getSimpleName();
    }
}
