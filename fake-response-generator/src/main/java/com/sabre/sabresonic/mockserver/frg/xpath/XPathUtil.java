package com.sabre.sabresonic.mockserver.frg.xpath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author lkwiatkowski
 */
public class XPathUtil {

    protected static final Logger log = Logger.getLogger(XPathUtil.class.getName());

    /**
     * Changes node value on specified path
     * @param xml Document
     * @param xPath Path to node
     * @param newValue
     * @return
     */
    public static String replaceNodeValue(String xml, String xPath, String newValue) {
        try {
            Document doc = XPathUtil.getDocument(xml);
            NodeList nodeList = XPathUtil.getNodes(doc, xPath);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                log.info("Replaced[" + xPath + "]: " + node.getTextContent() + " -> " + newValue);
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
    public static String insertTextNode(String xml, String xPath, String value) {
        try {
            Document doc = XPathUtil.getDocument(xml);
            NodeList nodeList = XPathUtil.getNodes(doc, xPath);
            if (nodeList.getLength() > 0) {
                Text a = doc.createTextNode(value);
                nodeList.item(nodeList.getLength() - 1).appendChild(a);
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
    public static String getContent(Document doc){
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
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

    public static NodeList getNodes(Object doc, String query) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(query);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        return nodes;
    }

    public static NodeList getNodes(Document doc, String query) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(query);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        return nodes;
    }

    public static NodeList getNodes(String xml, String query) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        return getNodes(getDocument(xml), query);
    }

    public static Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

}
