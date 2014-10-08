package com.sabre.sabresonic.mockserver.frg.generator;

import com.sabre.sabresonic.mockserver.frg.exception.FdgException;

/**
 * 
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public interface Generator {

    /**
     * Generates String data based on dataModel
     * @return 
     * @throws FdgException
     */
    String process() throws FdgException;
}
