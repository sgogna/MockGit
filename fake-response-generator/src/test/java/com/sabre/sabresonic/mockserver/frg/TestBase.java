package com.sabre.sabresonic.mockserver.frg;

import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class TestBase {

    public String readFile(String path) throws IOException {
        return FileUtils.readFileToString(FileUtils.toFile(getClass().getResource(path)));
    }

//    public boolean equals(Document doc1, Document doc2){
//        doc1.normalizeDocument();
//        doc2.normalizeDocument();
//        
//        return doc1.isEqualNode(doc2);
//    }
    public void assertXml(String expected, String actual) throws SAXException, IOException {
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(expected, actual);
        assertTrue("XML similar " + diff.toString(), diff.similar());
    }
}
