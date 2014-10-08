package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.config.Config;
import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.generator.SoapBasedFilenameGenerator;
import com.sabre.sabresonic.mockserver.core.generator.UriBasedFilenameGenerator;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.message.datagrabbers.DataGrabber;
import com.sabre.sabresonic.mockserver.core.message.replacers.DataReplacer;
import com.sabre.sabresonic.mockserver.core.message.replacers.DateReplaceEngine;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.service.beans.HostCommand;
import com.sabre.sabresonic.mockserver.core.service.beans.HostCommandFactory;
import com.sabre.sabresonic.mockserver.core.util.SpringBeanContainer;
import com.sabre.sabresonic.mockserver.core.util.XPathUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadSOAPFromFile extends AbstractService {

    private String basePath;
    private String request;
    private String response;
    private HostCommand hostCommand;
    private String sessionId;
    DateReplaceEngine dateReplaceEngine;
    DataReplacer dataReplacerComposite;
    DataGrabber dataGrabberComposite;
    private static final Logger LOG = LoggerFactory.getLogger(ReadSOAPFromFile.class);

    public ReadSOAPFromFile(final String basePath, final String request) {
        this.basePath = basePath;
        this.request = request;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        LOG.info("The Mode Set is REPLAY Mode!");
        dataGrabberComposite = SpringBeanContainer.getDataGrabber();
        dataReplacerComposite = SpringBeanContainer.getDataReplacer();
        dateReplaceEngine = SpringBeanContainer.getDateReplaceEngine();

        if (response == null) {
            response = "response";
        }
        if (request == null) {
            request = "request";
        }

        try {

            MockRequest mockRequest = (MockRequest) flowVariables.parseExpression(this.request);
            //start grabbing data
            dataGrabberComposite.grab(mockRequest);
            String basePathVal = null;
            if (basePath != null) {
                basePathVal = (String) flowVariables.parseExpression(this.basePath);
            }
            LOG.info("basePathValue is :::::: " + basePathVal);
            String resFileNameVal;

            String request = new String(mockRequest.getContent(), "UTF-8");

            LOG.info("Mock Request in REPLAY Mode :::: " + request);
            String fileName = getSoapAction(request);
            request = XPathUtil.prettyFormat(request, 2);
            hostCommand = HostCommandFactory.create(request);
            LOG.debug("host command after sanitizing :::: " + hostCommand);
            if (hostCommand !=null && hostCommand.getCommand() != "")
            {
                resFileNameVal = fileName + "RS_" + hostCommand;
            }
            else
            {
                resFileNameVal = fileName + "RS";
            }
            File resfile = new File(generatePath(String.valueOf(resFileNameVal), basePathVal));
            byte[] content = FileUtils.readFileToByteArray(resfile);

            //Date Replacment for checkin services
            sessionId =  getConversationId(request);
            content = dataReplacerComposite.replace(sessionId, new String(content, "UTF-8")).getBytes("UTF-8");
            if ((new String(content, "UTF-8").contains("ACS_") || new String(content, "UTF-8").contains("getReservation"))&& (Config.getCheckinDate()))
            {
                LOG.debug("INSIDE ACS RESPONSE TO CHANGE DATES: " + new String(content, "UTF-8") );
                content = dateReplaceEngine.replaceDatesWithCurrentDate(content);
                LOG.debug("INSIDE ACS RESPONSE AFTER CHANGE DATES: " + new String(content, "UTF-8") );
            }

            String str = new String(content, "UTF-8");
            LOG.info("Mock Response in REPLAY Mode ::: " + str);
            MockResponse mockResponse = new MockResponse();
            mockResponse.setContent(content);
            flowVariables.parseSetValueExpression(response, mockResponse);

        } catch (Exception ex) {
            throw new ServiceException(ex);
        }

    }

    protected String generatePath(String fileName, String basePath) {
        SoapBasedFilenameGenerator filenameGenerator = new SoapBasedFilenameGenerator();
        return filenameGenerator.generate(fileName, basePath);
    }

    protected String getSoapAction(String mockRequest)
    {
        String s = new String(mockRequest);
        int start = s.indexOf(":Action>");
        if (start < 0)
        {
            return getActionNameAfterBodyTag(s);
        }
        else
        {
            start += 8;
            int end = s.indexOf("<", start) - 2;
            return s.substring(start, end);
        }
    }

    private String getActionNameAfterBodyTag(String s)
    {
        int body = s.indexOf(":Body>");
        if (body > 0)
        {
            int endOfBody = body + 6;
            int maxCharOfActionName = 100;
            int canDidateActionNameEndIndex = endOfBody + maxCharOfActionName;
            String candidateActionName = s.substring(endOfBody, canDidateActionNameEndIndex).trim();
            Pattern pattern = Pattern.compile("[\\S]+");
            Matcher matcher = pattern.matcher(candidateActionName.replaceAll("<", ""));
            if (matcher.find())
            {
                String group = matcher.group();
                if (group.contains(":"))
                {
                    return group.substring(group.indexOf(":") + 1, group.length() - 2);
                }
                return group.substring(0, group.length() - 2);
            }
        }

        return null;
    }

    public String getConversationId(String mockRequest)
    {
        if (sessionId == null)
        {
            String rqnow = new String(mockRequest);
            String SessionIDStart_STR = ":ConversationId>";
            String SessionIDEnd_STR = "</";
            int SessionIDStart_INT;
            int SessionIDEnd_INT;
            String currentSessionID = "";

            SessionIDStart_INT = rqnow.indexOf(SessionIDStart_STR);
            if (SessionIDStart_INT > 0)
            {
                SessionIDEnd_INT = rqnow.indexOf(SessionIDEnd_STR, SessionIDStart_INT);
                sessionId = rqnow.substring(SessionIDStart_INT + 16, SessionIDEnd_INT);
                LOG.debug("\n Session ID :::::: " + currentSessionID + "<--");
            }
        }
        return sessionId;
    }


}
