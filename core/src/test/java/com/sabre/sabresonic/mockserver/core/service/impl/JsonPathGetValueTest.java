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
public class JsonPathGetValueTest {

    private final String JSON = "{ \"store\": {\n"
            + "    \"book\": [ \n"
            + "      { \"category\": \"reference\",\n"
            + "        \"author\": \"Nigel Rees\",\n"
            + "        \"title\": \"Sayings of the Century\",\n"
            + "        \"price\": 8.95\n"
            + "      },\n"
            + "      { \"category\": \"fiction\",\n"
            + "        \"author\": \"Evelyn Waugh\",\n"
            + "        \"title\": \"Sword of Honour\",\n"
            + "        \"price\": 12.99,\n"
            + "        \"isbn\": \"0-553-21311-3\"\n"
            + "      }\n"
            + "    ],\n"
            + "    \"bicycle\": {\n"
            + "      \"color\": \"red\",\n"
            + "      \"price\": 19.95\n"
            + "    }\n"
            + "  }\n"
            + "}";

    public Object executeQuery(String json, String jsonPath) throws IOException, OgnlException {
        FlowVariables flowVariables = new FlowVariables();
        flowVariables.put("json", json);
        flowVariables.put("jsonPath", jsonPath);
        JsonPathGetValue instance = new JsonPathGetValue("json", "jsonPath", "result");
        instance.execute(flowVariables);
        return flowVariables.get("result");
    }

    @Test
    public void testQueryAtribute() throws IOException, OgnlException {
        Object result = executeQuery(JSON, "$.store.book");
        assertTrue(result != null);
    }
}