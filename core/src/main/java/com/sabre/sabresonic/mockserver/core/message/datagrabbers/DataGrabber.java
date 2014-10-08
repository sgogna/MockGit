package com.sabre.sabresonic.mockserver.core.message.datagrabbers;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;

/**
 * DataGrabber
 */
public interface DataGrabber {
    public void grab(MockRequest request);
}
