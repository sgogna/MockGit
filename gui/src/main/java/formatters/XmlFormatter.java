package formatters;

import constants.DiffStyleConstants;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import util.XmlComparator;

public class XmlFormatter {

    public XmlFormatter() {
    }

    public static String format(String unformattedXml) {

        if (StringUtils.isBlank(unformattedXml)) {
            return unformattedXml;
        }
        try {
            return prettyPrint(unformattedXml);
        } catch (Exception e) {
            return unformattedXml;
        }

    }

    public static String prettyPrint(final String xml) {

        if (StringUtils.isBlank(xml)) {
            throw new RuntimeException("xml was null or blank in prettyPrint()");
        }

        final StringWriter sw;
        try {
            final OutputFormat format = OutputFormat.createPrettyPrint();

            final org.dom4j.Document document = DocumentHelper.parseText(xml);
            sw = new StringWriter();
            final XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
        } catch (Exception e) {
            throw new RuntimeException("Error pretty printing xml:\n" + xml, e);
        }
        return sw.toString();
    }
    
    private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String formatXML(Document document) {
        StringBuilder builder = new StringBuilder("");
        formatNode(document.getDocumentElement(), 0, builder);
        return DiffStyleConstants.HIGHLIGHT_STYLE + builder.toString();
    }

    private static void formatNode(Node node, int level, StringBuilder builder) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            if (node.getUserData(XmlComparator.DIFF_KEY) == XmlComparator.DIFFERENCE.CONTENT_DIFF) {
                builder.append(DiffStyleConstants.BEGIN_FORMAT);
                builder.append(HtmlFormatter.format(node.getNodeValue()));
                builder.append(DiffStyleConstants.END_FORMAT);
            } else {
                builder.append(HtmlFormatter.format(node.getNodeValue()));
            }
            return;
        }
        if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
            builder.append("&lt;![CDATA[");
            if (node.getUserData(XmlComparator.DIFF_KEY) == XmlComparator.DIFFERENCE.CONTENT_DIFF) {
                builder.append(DiffStyleConstants.BEGIN_FORMAT);
                builder.append(HtmlFormatter.format(node.getNodeValue()));
                builder.append(DiffStyleConstants.END_FORMAT);
            } else {
                builder.append(HtmlFormatter.format(node.getNodeValue()));
            }
            builder.append("]]&gt;");
            return;
        }

        if (level > 0) {
            builder.append(DiffStyleConstants.NEW_LINE);
        }
        if (node.getUserData(XmlComparator.DIFF_KEY) == XmlComparator.DIFFERENCE.NOT_MACHED) {
            builder.append(DiffStyleConstants.BEGIN_FORMAT);
        }
        for (int i = 0; i < level; i++) {
            builder.append("&nbsp;");
        }

        builder.append("&lt;");
        builder.append(node.getNodeName());
        formatAttributes(node, builder);
        if (node.getChildNodes().getLength() == 0) {
            builder.append("/&gt;");
        } else {
            builder.append("&gt;");
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                formatNode(node.getChildNodes().item(i), level + 4, builder);
            }
            if (!(node.getChildNodes().getLength() == 1 && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)) {
                builder.append(DiffStyleConstants.NEW_LINE);
                for (int i = 0; i < level; i++) {
                    builder.append("&nbsp;");
                }
            }
            builder.append("&lt;/");
            builder.append(node.getNodeName());
            builder.append("&gt;");
        }

        if (node.getUserData(XmlComparator.DIFF_KEY) == XmlComparator.DIFFERENCE.NOT_MACHED) {
            builder.append(DiffStyleConstants.END_FORMAT);
        }
    }

    private static void formatAttributes(Node node, StringBuilder builder) {
        if (node.getAttributes() == null) {
            return;
        }
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            if (node.getAttributes().item(i).getUserData(XmlComparator.DIFF_KEY) == XmlComparator.DIFFERENCE.NOT_MACHED) {
                builder.append(" ");
                builder.append(DiffStyleConstants.BEGIN_FORMAT);
                builder.append(node.getAttributes().item(i).getNodeName());
                builder.append("=\"");
                builder.append(node.getAttributes().item(i).getNodeValue());
                builder.append("\"");
                builder.append(DiffStyleConstants.END_FORMAT);

            } else if (node.getAttributes().item(i).getUserData(XmlComparator.DIFF_KEY) == XmlComparator.DIFFERENCE.CONTENT_DIFF) {
                builder.append(" ");
                builder.append(node.getAttributes().item(i).getNodeName());
                builder.append("=\"");
                builder.append(DiffStyleConstants.BEGIN_FORMAT);
                builder.append(node.getAttributes().item(i).getNodeValue());
                builder.append(DiffStyleConstants.END_FORMAT);
                builder.append("\"");

            } else {
                builder.append(" ");
                builder.append(node.getAttributes().item(i).getNodeName());
                builder.append("=\"");
                builder.append(node.getAttributes().item(i).getNodeValue());
                builder.append("\"");
            }
        }
    }
}
