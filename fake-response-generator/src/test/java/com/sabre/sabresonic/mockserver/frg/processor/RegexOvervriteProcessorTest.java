package com.sabre.sabresonic.mockserver.frg.processor;

import com.sabre.sabresonic.mockserver.frg.TestBase;
import com.sabre.sabresonic.mockserver.frg.generator.FreemarkerGenerator;
import com.sabre.sabresonic.mockserver.frg.generator.Generator;
import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import com.sabre.sabresonic.mockserver.frg.object.FrgResponse;
import freemarker.template.Configuration;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RegexOvervriteProcessorTest extends TestBase {

    RegexOvervriteProcessor regexOvervriteProcessor;
    String templatePath = "src/test/resources/regexOvervriteProcessorTest/template.ftl";
    String expectedXmlPath = "/regexOvervriteProcessorTest/expected.xml";

    @Before
    public void setUp() {
        regexOvervriteProcessor = new RegexOvervriteProcessor(new DefaultFdgProcessor());
        regexOvervriteProcessor.setGenerator(getGenerator());
    }

    public Generator getGenerator() {
        FreemarkerGenerator freemarkerGenerator = new FreemarkerGenerator();
        freemarkerGenerator.setConfiguration(new Configuration());
        freemarkerGenerator.setTemplatePath(templatePath);
        return freemarkerGenerator;
    }

    @Test
    public void testNullResponse() {
        FrgRequest fdgRequest = new FrgRequest();
        fdgRequest.setResponse(null);
        regexOvervriteProcessor.setProcessNullResponse(Boolean.FALSE);

        FrgResponse result = regexOvervriteProcessor.process(fdgRequest);
        assertEquals(null, result.getResponse());
    }

    @Test
    public void testNullResponse2() throws IOException, SAXException {
        FrgRequest fdgRequest = new FrgRequest();
        fdgRequest.setResponse(null);
        regexOvervriteProcessor.setProcessNullResponse(Boolean.TRUE);

        FrgResponse result = regexOvervriteProcessor.process(fdgRequest);
        assertXml(readFile(expectedXmlPath), result.getResponse());
    }
    
    @Test
    public void testRegexMatches() throws IOException, SAXException {
        FrgRequest fdgRequest = new FrgRequest();
        fdgRequest.setResponse("Hello world");
        regexOvervriteProcessor.setProcessNullResponse(Boolean.TRUE);
        regexOvervriteProcessor.setRegex(".*Hello.*");

        FrgResponse result = regexOvervriteProcessor.process(fdgRequest);
        assertXml(readFile(expectedXmlPath), result.getResponse());
    }
    @Test
    public void testRegexNotMatches() throws IOException, SAXException {
        FrgRequest fdgRequest = new FrgRequest();
        fdgRequest.setResponse("Hello world");
        regexOvervriteProcessor.setProcessNullResponse(Boolean.TRUE);
        regexOvervriteProcessor.setRegex(".*Sabre.*");

        FrgResponse result = regexOvervriteProcessor.process(fdgRequest);
        assertEquals("Hello world", result.getResponse());
    }
}
