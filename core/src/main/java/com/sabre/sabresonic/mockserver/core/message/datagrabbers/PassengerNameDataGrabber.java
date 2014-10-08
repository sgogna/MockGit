package com.sabre.sabresonic.mockserver.core.message.datagrabbers;

//import com.sabre.sabresonic.mockserver.core.beans.*;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.service.beans.HostCommand;
import com.sabre.sabresonic.mockserver.core.service.beans.PassengerNameCommand;
import com.sabre.sabresonic.mockserver.core.util.SpringBeanContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PassengerNameDataGrabber
 */
public class PassengerNameDataGrabber extends SabreCommandLLSDataGrabber {
    private static final Logger logger = LoggerFactory.getLogger(PassengerNameDataGrabber.class);

    @Override
    public void grab(MockRequest mockRequest) {
        HostCommand hostCommand = mockRequest.getHostCommand();
        if ( hostCommand.isPassengerNameCommand()) {
            String sessionId = mockRequest.getConversationId();
            SpringBeanContainer.getSessionsInfoManager().addPassengerName(sessionId, ((PassengerNameCommand) hostCommand).getPassengerName());
            logger.info("Received and stored passenger name: [" + sessionId + ":" + ((PassengerNameCommand) hostCommand).getPassengerName());
        }
    }
}
