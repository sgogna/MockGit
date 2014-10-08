/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.core.generator;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class UriBasedFilenameGeneratorTest {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UriBasedFilenameGeneratorTest.class);

    /**
     * Test of generate method, of class UriBasedFilenameGenerator.
     */
    @Test
    public void testGenerate() throws URISyntaxException {

//        URI uri = new URI("http://localhost:8084/testproxy/generic/record/lukasz/SSW2010/VAVA/webqtrip.html?execution=e1s1");
        URI uri = new URI("https://www.google.pl/webhp?hl=pl&tab=Tw#hl=pl&sclient=psy-ab&q=ala+ma+psa&oq=ala+ma+psa&gs_l=hp.3..0j0i8i10i30.2051.3857.0.4054.10.10.0.0.0.0.147.1236.1j9.10.0...0.0.0..1c.1.17.psy-ab.88NrOS8RFAk&pbx=1&bav=on.2,or.r_cp.r_qf.&bvm=bv.47883778,d.bGE&fp=e317cff82d2df5ed&biw=1680&bih=925");
//        URI uri = new URI("http://Users/sg0218182/.mockserver/store/record/soap/2013-07-11T00:00:00BRDFARES2");
        String basePath = "tablet/[A,  B]/";
        UriBasedFilenameGenerator instance = new UriBasedFilenameGenerator();

        String result = instance.generate(uri, basePath);
        LOG.info("result: " + result);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}