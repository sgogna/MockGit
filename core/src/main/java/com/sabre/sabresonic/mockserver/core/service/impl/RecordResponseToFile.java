package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.generator.UriBasedFilenameGenerator;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.message.datagrabbers.DataGrabber;
import com.sabre.sabresonic.mockserver.core.message.replacers.DataReplacer;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.io.File;
import java.net.URI;

import com.sabre.sabresonic.mockserver.core.util.SpringBeanContainer;
import org.apache.commons.io.FileUtils;

public class RecordResponseToFile extends AbstractService {

    private String basePath;
    private String request;
    private String response;
    private String fileName;

    public RecordResponseToFile(final String basePath, final String response, final String fileName) {
        this.basePath = basePath;
        this.response = response;
        this.fileName = fileName;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        if (response == null) {
            response = "response";
        }
        if (request == null) {
            request = "request";
        }

        if (fileName == null){
            fileName = "fileName";
        }


        try {
            String basePathVal = null;
            if (basePath != null) {
                basePathVal = (String) flowVariables.parseExpression(this.basePath);
            }

            MockRequest mockRequest = (MockRequest) flowVariables.parseExpression(this.request);
            MockResponse mockResponse = (MockResponse) flowVariables.parseExpression(this.response);

            File file = new File(generatePath(mockRequest.getURI(), basePathVal));
            FileUtils.writeByteArrayToFile(file, mockResponse.getContent());

        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    protected String generatePath(URI uri, String basePath) {
        UriBasedFilenameGenerator filenameGenerator = new UriBasedFilenameGenerator();
        return filenameGenerator.generate(uri, basePath);
    }
}
