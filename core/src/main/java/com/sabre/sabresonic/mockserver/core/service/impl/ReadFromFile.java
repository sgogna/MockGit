package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class ReadFromFile extends AbstractService {

    private String path;
    private String result;

    public ReadFromFile(final String path, final String result) {
        this.path = path;
        this.result = result;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            String filePath = (String) flowVariables.parseExpression(this.path);
            File file = new File(filePath);
            byte[] content = FileUtils.readFileToByteArray(file);
            flowVariables.parseSetValueExpression(result, content);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }

    }
}
