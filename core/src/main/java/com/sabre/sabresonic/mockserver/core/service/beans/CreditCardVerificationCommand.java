package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * Created with IntelliJ IDEA.
 * User: SG0962671
 * Date: 11/12/12
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreditCardVerificationCommand extends GenericHostCommand{
    public CreditCardVerificationCommand(String commandStr) {
        super(commandStr);
    }

    @Override
    public String toString() {
        return "CreditCardVerification";
    }

}
