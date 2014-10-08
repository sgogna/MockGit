package com.sabre.sabresonic.mockserver.core.message.datagrabbers;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;

/**
 * DataGrabberComposite
 */
public class DataGrabberComposite implements DataGrabber {
    @Autowired
    private List<DataGrabber> dataGrabbers;

    @Override
    public void grab(MockRequest mockRequest) {
        for (DataGrabber dataGrabber: dataGrabbers)
            dataGrabber.grab(mockRequest);
    }

    public void setDataGrabbers(List<DataGrabber> dataGrabbers) {
        this.dataGrabbers = dataGrabbers;
    }

    /**
     * Only used by unit test. application uses spring to set dataGrabbers
     * @param dataGrabber
     */
    public void add(DataGrabber dataGrabber) {
        if ( dataGrabbers == null )
            dataGrabbers = new ArrayList<DataGrabber>();
        dataGrabbers.add(dataGrabber);
    }
}
