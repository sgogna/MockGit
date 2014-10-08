package com.sabre.sabresonic.mockserver.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author lkwiatkowski
 */
public final class XPathUtil {

    private static final Logger LOG = LoggerFactory.getLogger(XPathUtil.class);

    private XPathUtil() {
    }

    /**
     * Changes node value on specified path
     * @param xml Document
     * @param xPath Path to node
     * @param newValue
     * @return
     */
    public static String replaceNodeValue(
            final String xml,
            final String xPath,
            final String newValue) {
        try {
            final Document doc = XPathUtil.getDocument(xml);
            final NodeList nodeList = XPathUtil.getNodes(doc, xPath);
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                LOG.info("Replaced[" + xPath + "]: " + node.getTextContent() + " -> " + newValue);
                node.setTextContent(newValue);
            }
            return XPathUtil.getContent(doc);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Inserts text node into specified xPath
     * @param xml Document
     * @param xPath Where the node should be inserted
     * @param value Value to insert
     * @return Xml document
     */
    public static String insertTextNode(final String xml, final String xPath, final String value) {
        try {
            final Document doc = XPathUtil.getDocument(xml);
            final NodeList nodeList = XPathUtil.getNodes(doc, xPath);
            if (nodeList.getLength() > 0) {
                final Text text = doc.createTextNode(value);
                nodeList.item(nodeList.getLength() - 1).appendChild(text);
            }
            return XPathUtil.getContent(doc);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Transform xml Document to String
     * @param doc Xml Document
     * @return String representation
     */
    public static String getContent(final Document doc) {
        try {
            final StringWriter sw = new StringWriter();
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    public static NodeList getNodes(final Object doc, final String query)
            throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final XPathExpression expr = xpath.compile(query);
        return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    }

    public static NodeList getNodes(final Document doc, final String query)
            throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final XPathExpression expr = xpath.compile(query);
        return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    }

    public static NodeList getNodes(final String xml, final String query)
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        return getNodes(getDocument(xml), query);
    }

    public static Document getDocument(final String xml)
            throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        final DocumentBuilder builder = domFactory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            LOG.error("Exception while formating xml :: ", e);
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

}
