/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sabre.sabresonic.mockserver.core;

import com.sabre.sabresonic.mockserver.core.matcher.CompositeMatcher;
import com.sabre.sabresonic.mockserver.core.matcher.RegexMatcher;
import com.sabre.sabresonic.mockserver.core.matcher.SimpleMatcher;
import com.sabre.sabresonic.mockserver.core.service.Service;
import com.sabre.sabresonic.mockserver.core.service.flowcontrol.IfExpression;
import com.sabre.sabresonic.mockserver.core.service.impl.*;
import com.sabre.sabresonic.mockserver.core.servicegroup.DefaultServiceGroup;
import com.thoughtworks.xstream.XStream;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public final class XstreamUtils {

    private XstreamUtils() {
    }

    public static XStream getXstream() {
        final XStream xstream = new XStream();

        xstream.alias("ServiceGroup", DefaultServiceGroup.class);

        xstream.alias("Sequence", Sequence.class);
        xstream.alias("Service", Service.class);
        xstream.alias("RecordToFile", RecordToFile.class);
        xstream.alias("OgnlExpression", OgnlExpression.class);
        xstream.alias("FilenameGenerator", FilenameGenerator.class);
        xstream.alias("InvokeHttp", InvokeHttp.class);
        xstream.alias("RequestUrlChanger", RequestUrlChanger.class);
        xstream.alias("If", IfExpression.class);
        xstream.alias("RegexMatcher", RegexMatcher.class);
        xstream.useAttributeFor(RegexMatcher.class, "pattern");
        xstream.alias("SimpleMatcher", SimpleMatcher.class);
        xstream.useAttributeFor(SimpleMatcher.class, "pattern");
        xstream.alias("CompositeMatcher", CompositeMatcher.class);
        xstream.addImplicitCollection(CompositeMatcher.class, "matchers");
        xstream.alias("RemoveQueryParam", RemoveQueryParam.class);
        xstream.alias("AddQueryParam", AddQueryParam.class);
        xstream.useAttributeFor(AddQueryParam.class, "name");
        xstream.useAttributeFor(AddQueryParam.class, "value");


        xstream.addImplicitCollection(Sequence.class, "steps");
//                    xstream.useAttributeFor("expression", IfExpression.class);
        xstream.useAttributeFor(IfExpression.class, "expression");
//                    xstream.useAttributeFor(IfExpression.class);
        xstream.useAttributeFor(OgnlExpression.class, "expression");

        xstream.alias("ReadResponseFromFile", ReadResponseFromFile.class);
        xstream.alias("RecordResponseToFile", RecordResponseToFile.class);
        xstream.alias("MixedModeSOAPToFile", MixedModeSOAPToFile.class);
        xstream.alias("TransparentModeSOAP", TransparentModeSOAP.class);
        xstream.alias("RecordSOAPToFile", RecordSOAPToFile.class);
        xstream.alias("ReadSOAPFromFile", ReadSOAPFromFile.class);

        xstream.alias("AddHeader", AddHeader.class);
        xstream.useAttributeFor(AddHeader.class, "name");
        xstream.useAttributeFor(AddHeader.class, "value");

        xstream.alias("ReplaceUrlPath", ReplaceUrlPath.class);

        xstream.alias("Timeout", Timeout.class);
        xstream.useAttributeFor(Timeout.class, "milis");

        xstream.alias("RemoveResponseHeader", RemoveResponseHeader.class);
        xstream.useAttributeFor(RemoveResponseHeader.class, "name");

        xstream.alias("RemoveRequestHeader", RemoveRequestHeader.class);
        xstream.useAttributeFor(RemoveRequestHeader.class, "name");

        xstream.alias("JsonPathGetValue", JsonPathGetValue.class);
        
        xstream.alias("XPathGetValue", XPathGetValue.class);
        xstream.alias("XPathInsertValue", XPathInsertValue.class);
        xstream.alias("XPathReplaceValue", XPathReplaceValue.class);
//        xstream.useAttributeFor(XPathValue.class, "name");

        xstream.alias("Invoke", Invoke.class);
        xstream.useAttributeFor(Invoke.class, "service");
//        xstream.addImplicitCollection(InvokeService.class, "params");

        xstream.alias("Param", Invoke.Param.class);
        xstream.useAttributeFor(Invoke.Param.class, "name");
        xstream.useAttributeFor(Invoke.Param.class, "value");

        return xstream;
    }
}
