package com.sabre.sabresonic.mockserver.frg.exception;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class FdgException extends RuntimeException{

    public FdgException(String message) {
        super(message);
    }

    public FdgException(Throwable cause) {
        super(cause);
    }

    public FdgException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
