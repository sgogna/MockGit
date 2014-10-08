/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.frg.processor;

import com.sabre.sabresonic.mockserver.frg.TestBase;
import com.sabre.sabresonic.mockserver.frg.generator.FreemarkerGenerator;
import com.sabre.sabresonic.mockserver.frg.generator.Generator;
import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;
import freemarker.template.Configuration;
import java.io.IOException;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class AddItemProcessorTest extends TestBase {

    AddItemProcessor addItemProcessor;
    String templatePath = "src/test/resources/addItemProcessorTest/template.ftl";
    String inputXmlPath = "/addItemProcessorTest/input.xml";
    String expectedXmlPath = "/addItemProcessorTest/expected.xml";
    String xPathToInsertItem = "/bookstore";

    @Before
    public void setUp() {
        addItemProcessor = new AddItemProcessor(new DefaultFdgProcessor()) {
            @Override
            public Integer generateNumItems() {
                return 2;
            }
        };
        addItemProcessor.setxPath(xPathToInsertItem);
        addItemProcessor.setItemGenerator(getItemGenerator());
    }

    public Generator getItemGenerator() {
        FreemarkerGenerator freemarkerGenerator = new FreemarkerGenerator();
        freemarkerGenerator.setConfiguration(new Configuration());
        freemarkerGenerator.setTemplatePath(templatePath);
        return freemarkerGenerator;
    }

    public FrgRequest getFdgRequest() throws IOException {
        FrgRequest fdgRequest = new FrgRequest();
        fdgRequest.setResponse(readFile(inputXmlPath));
        return fdgRequest;
    }

    /**
     * Test of process method, of class AddItemProcessor.
     */
    @Test
    public void testProcess() throws IOException, SAXException {
        String expectedXml = readFile(expectedXmlPath);

        FrgRequest fdgRequest = getFdgRequest();

        FrgResponse result = addItemProcessor.process(fdgRequest);

        assertXml(expectedXml, result.getResponse());
    }
}
