package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.registry.DefaultServiceRegistry;
import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.servicegroup.ServiceGroup;
import java.util.ArrayList;
import java.util.List;
import ognl.OgnlException;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class Invoke extends AbstractService {

    private String service;
    private List<Param> inputs = new ArrayList();
    private List<Param> outputs = new ArrayList();

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
        try {
            if (service != null) {
                String serviceName = String.valueOf(flowVariables.parseExpression(this.service));
                ServiceGroup service = DefaultServiceRegistry.getInstance().get(serviceName);
                if (service != null) {

                    FlowVariables localVariables = FlowVariables.createNew();
                    if (inputs != null) {
                        // map input parameters
                        for (Param param : inputs) {
                            param.doMap(flowVariables, localVariables);
                        }
                    }
                    
                    // invoke service
                    service.execute(localVariables);

                    if (outputs != null) {
                        //map output params
                        for (Param param : outputs) {
                            param.doMap(localVariables, flowVariables);
                        }
                    }
                } else {
                    getLogger().warn("Can not find service in registry: " + serviceName);
                }
            }
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    public void addInput(final String name, final String value) {
        inputs.add(new Param(name, value));
    }

    public void addOutput(final String name, final String value) {
        outputs.add(new Param(name, value));
    }

    public class Param {

        private String name;
        private String value;

        public Param(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        public void doMap(final FlowVariables input, final FlowVariables output) throws OgnlException {
            String mappedKey = String.valueOf(input.parseExpression(this.getName()));
            Object mappedValue = input.parseExpression(this.getValue());
            output.put(mappedKey, mappedValue);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
