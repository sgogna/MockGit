package com.sabre.sabresonic.mockserver.core.servicegroup;

import com.sabre.sabresonic.mockserver.core.XstreamUtils;
import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import java.io.File;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class FileServiceGroup implements ServiceGroup {

    private String name;
    private ServiceGroup serviceGroup;

    public FileServiceGroup(final String name, final File file) {
        serviceGroup = (ServiceGroup) XstreamUtils.getXstream().fromXML(file);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean canExecute(final MockRequest request) {
        return serviceGroup.canExecute(request);
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        serviceGroup.execute(flowVariables);
    }
}
