package com.sabre.sabresonic.mockserver.core.servicegroup;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.service.Service;

public interface ServiceGroup extends Service {
    String getName();
    boolean canExecute(MockRequest request);
}
