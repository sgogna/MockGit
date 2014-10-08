package com.sabre.sabresonic.mockserver.core.message.replacers

import groovy.xml.StreamingMarkupBuilder
//import testproxy.beans.PassengerName
import  com.sabre.sabresonic.mockserver.core.util.SpringBeanContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
    TravelItineraryReadPassengerNameReplacer
 */
class TravelItineraryReadPassengerNameReplacer implements DataReplacer {
    static final Logger logger = LoggerFactory.getLogger(TravelItineraryReadPassengerNameReplacer.class)

    String replace (String sessionId, String responseStr) {
        if ( !responseStr.contains("TravelItineraryRead")) {
            return responseStr;
        }
        def name = SpringBeanContainer.getSessionsInfoManager().getPassengerName(sessionId)
        if ( name == null ) {
            logger.info("No replacement names found. Returning original response")
            return responseStr
        }
        def root = new XmlSlurper().parseText(responseStr);
        if ( !root.Body.TravelItineraryReadRS.TravelItinerary.CustomerInfo.PersonName[0].Surname.equals(name.last) ) {
            root.Body.TravelItineraryReadRS.TravelItinerary.CustomerInfo.PersonName[0].Surname = name.last
            root.Body.TravelItineraryReadRS.TravelItinerary.CustomerInfo.PersonName[0].GivenName = name.first + ' ' + name.title
            return new StreamingMarkupBuilder(useDoubleQuotes: true).bind {
                mkp.declareNamespace("":"http://webservices.sabre.com/sabreXML/2011/10")
                mkp.yield root
            }.toString()
        }
        else {
            logger.info("Replacement name same as original name. returning original response")
            return responseStr
        }
    }
}

