package com.sabre.sabresonic.mockserver.core.message.datagrabbers;

import com.sabre.sabresonic.mockserver.core.http.MockRequest;
import com.sabre.sabresonic.mockserver.core.util.SpringBeanContainer;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AirBookRequestDataGrabber
 */
public class AirBookRequestDataGrabber implements DataGrabber {
    final Pattern dptrDateTimePattern = Pattern.compile("FlightSegment DepartureDateTime=\"(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})");

    private static final ThreadLocal<DateFormat> fromString
            = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }
    };

    private static final ThreadLocal<DateFormat> toString
            = new ThreadLocal<DateFormat>(){
        @Override
    protected DateFormat initialValue() {
            return new SimpleDateFormat("ddMMM");
        }
    };


    @Override
    public void grab(MockRequest mockRequest)
    {

        String requestString = null;
        try {
            requestString = new String(mockRequest.getContent(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if ( !requestString.contains("AirBook")) {
            return;
        }

        List<String> dptrDateList = new ArrayList<String>();
        Matcher matcher = dptrDateTimePattern.matcher(requestString);
        while (matcher.find()) {
            try {
                dptrDateList.add(toString.get().format(fromString.get().parse(matcher.group(1))).toUpperCase());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if ( !dptrDateList.isEmpty() ) {
            SpringBeanContainer.getSessionsInfoManager().setDepartureDates(mockRequest.getConversationId(),
                    dptrDateList.toArray(new String[dptrDateList.size()]));
        }
    }
}
