package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.generator.UriBasedFilenameGenerator;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.http.MockResponse;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.io.File;
import java.net.URI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class ReadResponseFromFile extends AbstractService {

    private String basePath;
    private String request;
    private String response;

    public ReadResponseFromFile(final String basePath, final String request) {
        this.basePath = basePath;
        this.request = request;
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

        try {
            MockRequest mockRequest = (MockRequest) flowVariables.parseExpression(this.request);

            String basePathVal = null;
            if (basePath != null) {
                basePathVal = (String) flowVariables.parseExpression(this.basePath);
            }
            String filePath = generatePath(mockRequest.getURI(), basePathVal);


            File file = new File(filePath);
            byte[] content = FileUtils.readFileToByteArray(file);
            getLogger().info(IOUtils.toString(content));
            MockResponse mockResponse = new MockResponse();
            mockResponse.setContent(content);
            flowVariables.parseSetValueExpression(response, mockResponse);

        } catch (Exception ex) {
            throw new ServiceException(ex);
        }

    }

    protected String generatePath(URI uri, String basePath) {
        UriBasedFilenameGenerator filenameGenerator = new UriBasedFilenameGenerator();
        return filenameGenerator.generate(uri, basePath);
    }
}
