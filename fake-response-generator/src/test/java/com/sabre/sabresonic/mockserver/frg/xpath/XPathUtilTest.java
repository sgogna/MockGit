package com.sabre.sabresonic.mockserver.frg.xpath;

import com.sabre.sabresonic.mockserver.frg.TestBase;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class XPathUtilTest extends TestBase{

    protected String xml;

    public XPathUtilTest() {
    }

    @Before
    public void setUp() throws IOException {
        xml = readFile("/xPathUtilTest/input.xml");
    }

    @Test
    public void xPathQueryTest() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException{
        NodeList nodeList = XPathUtil.getNodes(xml, "/bookstore/book[1]/title");
        assertEquals(1, nodeList.getLength());
        assertEquals("Everyday Italian", nodeList.item(0).getTextContent());
    }
    
    
}
