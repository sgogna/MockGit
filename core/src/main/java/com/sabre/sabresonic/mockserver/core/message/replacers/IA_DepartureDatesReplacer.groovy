package com.sabre.sabresonic.mockserver.core.message.replacers

import com.sabre.sabresonic.mockserver.core.util.SpringBeanContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
    IA_DepartureDatesReplacer
 */
class IA_DepartureDatesReplacer implements DataReplacer {
    static final Logger logger = LoggerFactory.getLogger(IA_DepartureDatesReplacer.class)

    @Override
    String replace(String sessionId, String originalResponse) {
        String[] newData = SpringBeanContainer.getSessionsInfoManager().getOriginDestinations(sessionId)
        if ( newData == null || newData.length == 0 )
            return originalResponse

        def matcher = originalResponse =~ / (\d{2}[A-Z]{3}) /
        def i=0
        while ( matcher.find() ) {
            if ( i >= newData.length ) {
                logger.warn("Out of bounds for newData. possible mismatch between number of O&Ds in canned response and request")
                break;
            }
            originalResponse = originalResponse.replace(matcher.group(1), newData[i++])
        }
        return originalResponse
    }
}
