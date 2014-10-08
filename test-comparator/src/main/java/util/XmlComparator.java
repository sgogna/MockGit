/* Copyright 2009 EB2 International Limited */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sabre.ssw.proxy.concurrent.managment.ConcurrentXMLTag;

public class XmlComparator
{
    public static String DIFF_KEY = "diff";

    public static enum DIFFERENCE
    {
        CONTENT_DIFF, NOT_MACHED
    };

    private List<String> skippedTags = new LinkedList<String>();
    // String[0] - tag
    // String[1] - attribute
    private List<String[]> skippedAttributes = new LinkedList<String[]>();
    private boolean skipNamespace = false;

    public void setSkippedTags(List<String> skippedTags)
    {
        this.skippedTags = skippedTags;
    }

    public void setSkippedAttributes(List<String[]> skippedAttributes)
    {
        this.skippedAttributes = skippedAttributes;
    }

    public void setSkipNamespace(boolean skipNamespace)
    {
        this.skipNamespace = skipNamespace;
    }

    public boolean compareXmlFiles(File firstFile, File secondFile) throws SAXException
    {
        boolean result = false;
        String xmlContent1 = getOneLineContent(firstFile);
        String xmlContent2 = getOneLineContent(secondFile);
        Document document1 = parseXml(xmlContent1);
        Document document2 = parseXml(xmlContent2);
        result = compareNodes(document1.getDocumentElement(), document2.getDocumentElement());
        return result;
    }

    public Pair<Document> compareXmlFilesWithDiffSelection(File firstFile, File secondFile)
    {
        Document document1 = null;
        try
        {
            String xmlContent1 = getOneLineContent(firstFile);
            document1 = parseXml(xmlContent1);
        }
        catch (Exception e)
        {
        }
        Document document2 = null;
        try
        {
            String xmlContent2 = getOneLineContent(secondFile);
            document2 = parseXml(xmlContent2);
        }
        catch (Exception e)
        {
        }
        if (document1 != null && document2 != null)
        {
            compareNodeWithDiffSelection(document1.getDocumentElement(), document2.getDocumentElement());
        }
        return new Pair<Document>(document1, document2);
    }

    private static String removeNamespace(Boolean skipNamespace, String value)
    {
        if (skipNamespace)
        {
            value = value.substring(value.indexOf(':') + 1);
        }
        return value;
    }

    private boolean isTagSkipped(String tagName)
    {
        for (Iterator<String> iterator = skippedTags.iterator(); iterator.hasNext();)
        {
            String skippedTagRegex = iterator.next();
            if (tagName.matches(skippedTagRegex))
            {
                return true;
            }
        }
        return false;
    }

    private Map<String, String> getNotSkippedAttributes(NamedNodeMap attributes)
    {
        Map<String, String> notSkippedAttributes = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); i++)
        {
            boolean isSkipped = false;
            Attr attribute = (Attr) attributes.item(i);
            String tagName = attribute.getOwnerElement().getNodeName();
            String attributeName = attribute.getNodeName();
            // TODO
            // remove hardcoded name
            if (skipNamespace && attributeName.startsWith("xmlns:"))
            {
                continue;
            }
            tagName = removeNamespace(skipNamespace, tagName);
            attributeName = removeNamespace(skipNamespace, attributeName);
            for (Iterator<String[]> iterator = skippedAttributes.iterator(); iterator
                    .hasNext();)
            {
                String[] skippedAttribute = iterator.next();
                String tagRegex = skippedAttribute[0];
                String attributeRegex = skippedAttribute[1];
                if (tagName.matches(tagRegex) && attributeName.matches(attributeRegex))
                {
                    isSkipped = true;
                    break;
                }
            }
            if (!isSkipped)
            {
                String attributeValue = attribute.getNodeValue();
                notSkippedAttributes.put(attributeName, attributeValue);
            }
        }
        return notSkippedAttributes;
    }

    private boolean compareAttributes(Node node1, Node node2)
    {
        NamedNodeMap attributes1 = node1.getAttributes();
        NamedNodeMap attributes2 = node2.getAttributes();
        if (attributes1 == null && attributes2 == null)
        {
            return true;
        }
        else if (attributes1 == null || attributes2 == null)
        {
            return false;
        }
        Map<String, String> notSkippedAttributes1 = getNotSkippedAttributes(attributes1);
        Map<String, String> notSkippedAttributes2 = getNotSkippedAttributes(attributes2);
        return equalMaps(notSkippedAttributes1, notSkippedAttributes2);
    }

    private boolean equalMaps(Map<String, String> map1, Map<String, String> map2)
    {
        if (map1 == null || map2 == null || map1.size() != map2.size())
        {
            return false;
        }
        Set<String> keySet1 = map1.keySet();
        Set<String> keySet2 = map2.keySet();
        if (!keySet1.equals(keySet2))
        {
            return false;
        }
        for (Iterator<String> iterator = keySet1.iterator(); iterator.hasNext();)
        {
            Object key = iterator.next();
            Object value1 = map1.get(key);
            Object value2 = map2.get(key);
            if (!value1.equals(value2))
            {
                return false;
            }
        }
        return true;
    }

    private boolean compareNodes(Node node1, Node node2)
    {
        String nodeName1 = node1.getNodeName();
        String nodeName2 = node2.getNodeName();
        nodeName1 = removeNamespace(skipNamespace, nodeName1);
        nodeName2 = removeNamespace(skipNamespace, nodeName2);
        if (nodeName1.equals(nodeName2))
        {
            if (isTagSkipped(nodeName1))
            {
                return true;
            }
        }
        else
        {
            return false;
        }
        if (node1.getNodeType() == Node.TEXT_NODE || node1.getNodeType() == Node.CDATA_SECTION_NODE)
        {
            return node1.getTextContent().trim().equals(node2.getTextContent().trim());
        }
        if (!compareAttributes(node1, node2))
        {
            return false;
        }
        NodeList children1 = node1.getChildNodes();
        NodeList children2 = node2.getChildNodes();
        if (children1 != null && children2 != null && children1.getLength() == children2.getLength())
        {
            for (int i = 0; i < children1.getLength(); i++)
            {
                if (!compareNodes(children1.item(i), children2.item(i)))
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void compareAttributesWithDiffSelection(Node node1, Node node2)
    {
        NamedNodeMap attributes1 = node1.getAttributes();
        NamedNodeMap attributes2 = node2.getAttributes();
        if (!(attributes1 == null) && attributes2 == null)
        {
            Map<String, String> notSkippedAttributes1 = getNotSkippedAttributes(attributes1);
            for (int i = 0; i < attributes1.getLength(); i++)
            {
                if (notSkippedAttributes1.containsKey(removeNamespace(skipNamespace, attributes1.item(i).getNodeName())))
                {
                    attributes1.item(i).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
                }
            }

        }
        else if (attributes1 == null && !(attributes2 == null))
        {
            Map<String, String> notSkippedAttributes2 = getNotSkippedAttributes(attributes2);
            for (int i = 0; i < attributes2.getLength(); i++)
            {
                if (notSkippedAttributes2.containsKey(removeNamespace(skipNamespace, attributes2.item(i).getNodeName())))
                {
                    attributes2.item(i).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
                }
            }
        }
        else if (!(attributes1 == null) && !(attributes2 == null))
        {
            Map<String, String> notSkippedAttributes1 = getNotSkippedAttributes(attributes1);
            Map<String, String> notSkippedAttributes2 =
                    getNotSkippedAttributes(attributes2);

            int lastpass2 = 0;
            for (int i = 0; i < attributes1.getLength(); i++)
            {
                boolean pass = false;
                for (int j = lastpass2; j < attributes2.getLength(); j++)
                {
                    if (removeNamespace(skipNamespace, attributes1.item(i).getNodeName().trim()).equals(removeNamespace(skipNamespace, attributes2.item(j).getNodeName().trim())))
                    {
                        for (int h = lastpass2; h < j; h++)
                        {
                            if (notSkippedAttributes2.containsKey(removeNamespace(skipNamespace, attributes2.item(h).getNodeName())))
                            {
                                attributes2.item(h).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
                            }
                        }
                        lastpass2 = j + 1;
                        pass = true;
                        if (notSkippedAttributes1.containsKey(removeNamespace(skipNamespace, attributes1.item(i).getNodeName())))
                        {
                            if (!attributes1.item(i).getNodeValue().trim().equals(attributes2.item(j).getNodeValue().trim()))
                            {
                                attributes1.item(i).setUserData(DIFF_KEY, DIFFERENCE.CONTENT_DIFF, null);
                                attributes2.item(j).setUserData(DIFF_KEY, DIFFERENCE.CONTENT_DIFF, null);
                            }

                        }
                        break;

                    }
                }
                if (!pass && notSkippedAttributes1.containsKey(removeNamespace(skipNamespace, attributes1.item(i).getNodeName())))
                {
                    attributes1.item(i).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
                }
            }

        }

    }

    private void compareNodeWithDiffSelection(Node node1, Node node2)
    {
        compareAttributesWithDiffSelection(node1, node2);
        if (node1.getNodeType() == Node.TEXT_NODE || node1.getNodeType() == Node.CDATA_SECTION_NODE)
        {
            if (!node1.getTextContent().trim().equals(node2.getTextContent().trim()))
            {
                node1.setUserData(DIFF_KEY, DIFFERENCE.CONTENT_DIFF, null);
                node2.setUserData(DIFF_KEY, DIFFERENCE.CONTENT_DIFF, null);
            }
            return;
        }

        NodeList children1 = node1.getChildNodes();
        NodeList children2 = node2.getChildNodes();
        List<Pair<Node>> pairs = prepareChildrenPairs(children1, children2);
        for (Pair<Node> pair : pairs)
        {
            compareNodeWithDiffSelection(pair.getValue1(), pair.getValue2());
        }
    }

    private List<Pair<Node>> prepareChildrenPairs(NodeList children1, NodeList children2)
    {
        List<Pair<Node>> pairs = new LinkedList<Pair<Node>>();
        if (!(children1 == null) && children2 == null)
        {
            for (int i = 0; i < children1.getLength(); i++)
            {
                children1.item(i).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
            }
        }
        else if (children1 == null && !(children2 == null))
        {
            for (int i = 0; i < children2.getLength(); i++)
            {
                children2.item(i).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
            }
        }
        else if (!(children1 == null) && !(children2 == null))
        {
            int lastpass2 = 0;
            for (int i = 0; i < children1.getLength(); i++)
            {
                boolean pass = false;
                for (int j = lastpass2; j < children2.getLength(); j++)
                {
                    if (removeNamespace(skipNamespace, children1.item(i).getNodeName().trim()).equals(removeNamespace(skipNamespace, children2.item(j).getNodeName().trim())))
                    {
                        for (int h = lastpass2; h < j; h++)
                        {
                            children2.item(h).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
                        }
                        lastpass2 = j + 1;
                        pass = true;
                        if (!isTagSkipped(removeNamespace(skipNamespace, children1.item(i).getNodeName())))
                        {
                            pairs.add(new Pair<Node>(children1.item(i), children2.item(j)));
                        }
                        break;

                    }
                }
                if (!pass)
                {
                    children1.item(i).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
                }
            }
            for (int j = lastpass2; j < children2.getLength(); j++)
            {
                children2.item(j).setUserData(DIFF_KEY, DIFFERENCE.NOT_MACHED, null);
            }
        }

        return pairs;
    }

    public static Document parseXml(String content) throws SAXException
    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setCoalescing(false);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setIgnoringComments(true);
        DocumentBuilder documentBuilder;

        Document document = null;
        try
        {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            // TODO implement it.
            documentBuilder.setErrorHandler(new ErrorHandler()
            {

                @Override
                public void warning(SAXParseException exception) throws SAXException
                {
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException
                {
                }

                @Override
                public void error(SAXParseException exception) throws SAXException
                {
                }
            });
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(content));
            document = documentBuilder.parse(is);
        }
        catch (ParserConfigurationException e)
        {
            // TODO
            // implement error handling
        }
        catch (IOException e)
        {
            // TODO
            // implement error handling
        }
        return document;
    }

    private static String getOneLineContent(File file)
    {
        BufferedReader br = null;
        String line;
        StringBuilder sb = new StringBuilder();
        try
        {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null)
            {
                sb.append(line.trim());
            }
        }
        catch (FileNotFoundException e)
        {
            // TODO
            // implement error handling
        }
        catch (IOException e)
        {
            // TODO
            // implement error handling
        }
        finally {
        	if ( br != null ) {
        		try {
					br.close();
				} catch (IOException e) {
					
				}
        	}
        }
        return sb.toString();
    }

    public static String getXMLWithSkippedTags(String xmlContent, List<ConcurrentXMLTag> taglist)
    {
        Document document = null;
        try
        {
            document = parseXml(xmlContent);
            removeSkippedTags(document.getDocumentElement(), taglist);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            return result.getWriter().toString().replaceAll("\n|\r", "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";

    }

    private static void removeSkippedTags(Node n, List<ConcurrentXMLTag> taglist)
    {
        removeAttribute(n, taglist);
        List<Node> toDelete = new ArrayList<Node>();
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            for (Iterator<ConcurrentXMLTag> iterator = taglist.iterator(); iterator.hasNext();)
            {
                ConcurrentXMLTag concurrentXMLTag = (ConcurrentXMLTag) iterator.next();
                if (removeNamespace(true, children.item(i).getNodeName()).matches(concurrentXMLTag.getTag()))
                {
                    toDelete.add(children.item(i));
                }
                else
                {
                    removeSkippedTags(children.item(i), taglist);
                }
            }
        }

        for (Iterator<Node> iterator = toDelete.iterator(); iterator.hasNext();)
        {
            Node node = (Node) iterator.next();
            n.removeChild(node);
        }
    }

    private static void removeAttribute(Node n, List<ConcurrentXMLTag> taglist)
    {
        NamedNodeMap attributes = n.getAttributes();
        if (attributes == null)
        {
            return;
        }
        List<Node> toDelete = new ArrayList<Node>();
        for (int i = 0; i < attributes.getLength(); i++)
        {
            for (Iterator<ConcurrentXMLTag> iterator = taglist.iterator(); iterator.hasNext();)
            {
                ConcurrentXMLTag concurrentXMLTag = (ConcurrentXMLTag) iterator.next();
                if (removeNamespace(true, n.getNodeName()).matches(concurrentXMLTag.getTag())
                        && removeNamespace(true, attributes.item(i).getNodeName()).matches(concurrentXMLTag.getAttributes()))
                {
                    toDelete.add(attributes.item(i));
                }
            }
        }
        for (Iterator<Node> iterator = toDelete.iterator(); iterator.hasNext();)
        {
            Node node = (Node) iterator.next();
            n.removeChild(node);
        }
    }

}
