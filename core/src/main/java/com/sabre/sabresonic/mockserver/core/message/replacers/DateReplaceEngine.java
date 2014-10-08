/* Copyright 2009 EB2 International Limited */
package com.sabre.sabresonic.mockserver.core.message.replacers;


import com.sabre.sabresonic.mockserver.core.message.replacers.patterns.DatePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class DateReplaceEngine
{

    private static final Logger LOG = LoggerFactory.getLogger(DateReplaceEngine.class);
    private List<DatePattern> patternList;

    public static String getFormatedDate(long time)
    {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss,SSS");
        Date d = new Date();
        d.setTime(time);
        return dateFormat.format(d);
    }



    public List<DatePattern> getPatternList()
    {
        return patternList;
    }

    public void setPatternList(List<DatePattern> patternList)
    {
        this.patternList = patternList;
    }


    public byte[] replaceDates(byte[] response, long difftime)
    {
        Charset charset = Charset.forName("UTF-8");
        String responseMsg = new String(response, charset);
        for (DatePattern dp : patternList)
        {
            responseMsg = dp.replaceDates(difftime, responseMsg);
        }

        return responseMsg.getBytes(charset);
    }


    public byte[] replaceDatesWithCurrentDate(byte[] response)
    {
        Charset charset = Charset.forName("UTF-8");
        String responseMsg = new String(response, charset);
        for (DatePattern dp : patternList)
        {
            responseMsg = dp.replaceDatesWithCurrentDate(responseMsg);
        }

        return responseMsg.getBytes(charset);
    }
}
