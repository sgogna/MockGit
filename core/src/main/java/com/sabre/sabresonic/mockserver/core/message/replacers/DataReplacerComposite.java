package com.sabre.sabresonic.mockserver.core.message.replacers;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * DataReplacerComposite
 */
public class DataReplacerComposite implements DataReplacer {
    @Autowired
    private List<DataReplacer> dataReplacers;

    @Override
    public String replace(String sessionId, String originalResponse) {
        String modifiedResponse = originalResponse;
        for ( DataReplacer dataReplacer: dataReplacers ) {
            modifiedResponse = dataReplacer.replace(sessionId, modifiedResponse);
        }
        return modifiedResponse;
    }

    public void setDataReplacers(List<DataReplacer> dataReplacers) {
        this.dataReplacers = dataReplacers;
    }

    /**
     * This method is only used by the unit test. application uses spring to set the dataReplacers
     * @param dataReplacer
     */
    public void add(DataReplacer dataReplacer) {
        if (dataReplacers == null)
            dataReplacers = new ArrayList<DataReplacer>();
        dataReplacers.add(dataReplacer);
    }
}
