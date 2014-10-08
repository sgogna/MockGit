package com.sabre.sabresonic.mockserver.core.service.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HostCommandFactory
 */
public class HostCommandFactory {

    private static final Pattern TICKETING_BPC_PATTERN = Pattern.compile("8(\\d){4}/(\\d){2}[a-zA-Z]{3}");

    /**
     * Returns the appropriate HostCommand based on the command string
     * VERY VERY FRAGILE CODE THAT DEPENDS ON THE ORDER OF THE IF STATEMENTS
     * WILL BE REFACTORED SOON
     * @param requestContent the request xml string
     * @return the appropriate HostCommand object
     */
    public static HostCommand create(String requestContent) {
        Pattern PASS_PATTERN = Pattern.compile("<.*(?:(?:HostCommand)|(?:Request))>(.*?)<.*>");
        Matcher m = PASS_PATTERN.matcher(requestContent);
        String commandStr = "";
        if (m.find()) {
            commandStr = m.group(1);
            Matcher ticketingBPCMatcher = TICKETING_BPC_PATTERN.matcher(commandStr);
            if ( commandStr.startsWith("-1") ) {
                return new PassengerNameCommand(commandStr);
            }
            // Form of payment input
            else if ( commandStr.startsWith("5-")) {
                return new FOPEntryCommand(commandStr);
            }
            // Cancel seats, or...
            else if ( commandStr.startsWith("4GX") ) {
                return new CancelSeatsCommand(commandStr);
            }
            // Book seats, or...
            else if ( commandStr.startsWith("4G") && commandStr.contains("/")) {
                return new BookSeatsCommand(commandStr);
            }
            // Must be a display seats command
            else if ( commandStr.startsWith("4G")) {
                return new DisplaySeatMapCommand(commandStr);
            }
            else if ( commandStr.startsWith("4DOCS") || commandStr.startsWith("3DOCS") ) {
                return new SecureFlightCommand(commandStr);
            }
            else if ( commandStr.startsWith("CC") && commandStr.contains("PAY/TRN") ) {
                return new MSRExchangeCommand(commandStr);
            }
            else if ( commandStr.startsWith("CC") && commandStr.contains("PAY/FOP")) {
                return new FOPCommand(commandStr);
            }
            else if ( commandStr.startsWith("W") && commandStr.contains("F*")) {
                return new TicketingCCFOPCommand(commandStr);
            }
            else if (ticketingBPCMatcher.matches()) {
                return new TicketingBPCCommand(commandStr);
            }
            else if ( commandStr.startsWith("PE") && commandStr.contains("@")) {
                return new EmailIdCommand(commandStr);
            }
            else if ( commandStr.startsWith("CK*") ) {
                return new CreditCardVerificationCommand(commandStr);
            }
        }
        return new GenericHostCommand(commandStr);
    }
}
