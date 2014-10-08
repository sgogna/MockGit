package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class RecordToFile extends AbstractService {

    private String path;
    private String toRecord;

    public RecordToFile(final String path, final String toRecord) {
        this.path = path;
        this.toRecord = toRecord;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            String filePath = (String) flowVariables.parseExpression(this.path);
            Object toRecordEval = flowVariables.parseExpression(this.toRecord);
            final File file = new File(filePath);
            getLogger().debug("saving content into: " + file.getAbsolutePath());
            if (toRecordEval instanceof byte[]) {
                FileUtils.writeByteArrayToFile(file, (byte[]) toRecordEval);
            } else {
                FileUtils.write(file, toRecordEval.toString());
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }

    }
}
