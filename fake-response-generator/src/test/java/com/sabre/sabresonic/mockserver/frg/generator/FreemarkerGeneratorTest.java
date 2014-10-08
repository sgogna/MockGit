/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.frg.generator;

import com.sabre.sabresonic.mockserver.frg.TestBase;
import freemarker.template.Configuration;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class FreemarkerGeneratorTest extends TestBase {

    protected Configuration configuration;
    protected FreemarkerGenerator freemarkerGenerator;

    @Before
    public void setUp() {
        configuration = new Configuration();
        freemarkerGenerator = new FreemarkerGenerator();
        freemarkerGenerator.setConfiguration(configuration);
    }

    public Map<String, Object> getDataModel() {
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("name", "John");
        user.put("surname", "Smith");

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("city", "Warsaw");

        return model;
    }

    @Test
    public void testProcessDataModel() throws IOException, ParserConfigurationException, SAXException {
        freemarkerGenerator.setTemplatePath("src/test/resources/freemarkerGeneratorTest/template.ftl");
        freemarkerGenerator.setDataModel(getDataModel());
        String result = freemarkerGenerator.process();
        String expected = readFile("/freemarkerGeneratorTest/expected.xml");
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(expected, result);
        assertTrue("XML similar " + diff.toString(), diff.similar());
    }
    
//    @Test
//    public void testRegex() {
//        String word = "java.net.ConnectException: Connection timed out: connect"
//	+"\nat java.net.DualStackPlainSocketImpl.connect0(Native Method)"
//	+"\nat java.net.DualStackPlainSocketImpl.socketConnect(DualStackPlainSocketImpl.java:69)"
//	+"\nat java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:339)"
//	+"\nat java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:200)"
//	+"\nat java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:182)";
//        String regex = ".*doConnect.*";
//        
//        Pattern patern = Pattern.compile(regex, Pattern.DOTALL);
//        assert patern.matcher(word).matches();
//        
//    }
    
    
    
}
