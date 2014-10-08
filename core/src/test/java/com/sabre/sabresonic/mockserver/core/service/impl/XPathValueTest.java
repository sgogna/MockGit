package com.sabre.sabresonic.mockserver.core.service.impl;


import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.util.xpath.MockNode;
import java.io.IOException;
import java.util.List;

import ognl.OgnlException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class XPathValueTest {

    private final String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
            + "<bookstore>\n"
            + "  <book category=\"cooking\">\n"
            + "    <title lang=\"en\">Everyday Italian</title>\n"
            + "    <author>Giada De Laurentiis</author>\n"
            + "    <year>2005</year>\n"
            + "    <price>30.00</price>\n"
            + "  </book>\n"
            + "  <book category=\"children\">\n"
            + "    <title lang=\"en\">Harry Potter</title>\n"
            + "    <author>J K. Rowling</author>\n"
            + "    <year>2005</year>\n"
            + "    <price>29.99</price>\n"
            + "  </book>\n"
            + "  <book category=\"web\">\n"
            + "    <title lang=\"en\">XQuery Kick Start</title>\n"
            + "    <author>James McGovern</author>\n"
            + "    <author>Per Bothner</author>\n"
            + "    <author>Kurt Cagle</author>\n"
            + "    <author>James Linn</author>\n"
            + "    <author>Vaidyanathan Nagarajan</author>\n"
            + "    <year>2003</year>\n"
            + "    <price>49.99</price>\n"
            + "  </book>\n"
            + "  <book category=\"web\" cover=\"paperback\">\n"
            + "    <title lang=\"en\">Learning XML</title>\n"
            + "    <author>Erik T. Ray</author>\n"
            + "    <year>2003</year>\n"
            + "    <price>39.95</price>\n"
            + "  </book>\n"
            + "</bookstore>";

    public List<MockNode> executeQuery(String xml, String xPathQuery) throws IOException, OgnlException {
        FlowVariables flowVariables = new FlowVariables();
        flowVariables.put("xml", XML);
        flowVariables.put("xPath", xPathQuery);
        XPathGetValue instance = new XPathGetValue("xml", "xPath", "result");
        instance.execute(flowVariables);
        List<MockNode> nodeList = (List) flowVariables.get("result");
        return nodeList;
    }

    @Test
    public void testQueryAtribute() throws IOException, OgnlException {
        List<MockNode> nodes = executeQuery(XML, "//title/@lang");
        assertTrue(nodes.size() == 4);
        for (MockNode mockNode : nodes) {
            assertEquals("lang", mockNode.getName());
            assertEquals("en", mockNode.getValue());
            assertEquals("en", mockNode.getText());
        }
    }
}