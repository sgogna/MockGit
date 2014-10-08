package com.sabre.sabresonic.mockserver.core.util.xpath;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class MockNode {

    private final transient Node node;

    public MockNode(final Node node) {
        Validate.notNull(node);
        this.node = node;
    }

    public String getName() {
        return node.getNodeName();
    }

    public String getValue() {
        return node.getNodeValue();
    }

    public String getText() {
        return node.getTextContent();
    }

    public Map getAttr() {
        final Map map = new HashMap();

        final NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            final Node attr = attributes.item(i);
            map.put(attr.getNodeName(), attr.getNodeValue());
        }
        return map;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
