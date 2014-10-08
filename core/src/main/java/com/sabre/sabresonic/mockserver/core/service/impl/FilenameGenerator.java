package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.generator.UriBasedFilenameGenerator;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.net.URI;

public class FilenameGenerator extends AbstractService {

    private String basePath;
    private String uri;
    private String result;

    public FilenameGenerator(final String uri, final String basePath, final String result) {
        this.uri = uri;
        this.basePath = basePath;
        this.result = result;
    }
    
    public FilenameGenerator(final String uri, final String result) {
        this(uri, null, result);
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            UriBasedFilenameGenerator filenameGenerator = new UriBasedFilenameGenerator();
            String basePathVal = (String)flowVariables.parseExpression(this.basePath);
            getLogger().info("basePathVal= " + basePathVal);
            URI uriVal = (URI) flowVariables.parseExpression(this.uri);
            getLogger().info("uriVal= " + uriVal);
            getLogger().info("filenameGenerator= " + filenameGenerator);
            String generatedPath = filenameGenerator.generate(uriVal, basePathVal);
            flowVariables.parseSetValueExpression(result, generatedPath);

        } catch (Exception ex) {
            throw new ServiceException(ex);
        }


    }
}
