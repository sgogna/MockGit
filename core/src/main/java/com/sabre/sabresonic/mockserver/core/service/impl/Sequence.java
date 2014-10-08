package com.sabre.sabresonic.mockserver.core.service.impl;

import com.sabre.sabresonic.mockserver.core.service.AbstractService;
import com.sabre.sabresonic.mockserver.core.service.FlowVariables;
import com.sabre.sabresonic.mockserver.core.service.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Sequence extends AbstractService {

    private List<Service> steps = new ArrayList<Service>();

    public void setSteps(final List<Service> steps) {
        this.steps = steps;
    }

    public Sequence addStep(final Service step) {
        this.steps.add(step);
        return this;
    }

    @Override
    public void execute(final FlowVariables flowVariables) {
        super.execute(flowVariables);
         for (Service step : steps) {
            step.execute(flowVariables);
        }
    }

    public Collection<Service> getSteps() {
        return steps;
    }

}