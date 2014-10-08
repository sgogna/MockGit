/* Copyright 2009 EB2 International Limited */
package com.sabre.sabresonic.mockserver.core.message.replacers.patterns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePattern3 extends AbstractDatePattern
{
    private static Pattern DATE_PATTERN = Pattern.compile("[0-3]\\d(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)((\\d\\d)?)");
    private static final String DATE_FORMAT1="ddMMM";
    private static final String DATE_FORMAT2="ddMMMyy";
    private static final Logger LOG = LoggerFactory.getLogger(DatePattern3.class);

    @Override
    public String replaceDates(long difftime, String responseMsg)
    {
        SimpleDateFormat format1 = new SimpleDateFormat(DATE_FORMAT1);
        SimpleDateFormat format2 = new SimpleDateFormat(DATE_FORMAT2);
        Matcher m = DATE_PATTERN.matcher(responseMsg);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            String foundText = responseMsg.substring(m.start(), m.end());
            try
            {
                if (!m.group(m.groupCount() - 1).isEmpty())
                {
                    Date d = format2.parse(foundText);
                    d = new Date(d.getTime() + difftime);
                    m.appendReplacement(sb, format2.format(d));
                }
                else
                {
                    Date d = format1.parse(foundText);
                    d = new Date(d.getTime() + difftime);
                    m.appendReplacement(sb, format1.format(d));
                }
            }
            catch (Exception e)
            {
                LOG.error("Can't convert string: " + foundText + " to date!", e);
                m.appendReplacement(sb, foundText);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @Override
    public String replaceDatesWithCurrentDate(String responseMsg)
    {
        SimpleDateFormat format1 = new SimpleDateFormat(DATE_FORMAT1);
        SimpleDateFormat format2 = new SimpleDateFormat(DATE_FORMAT2);
        Matcher m = DATE_PATTERN.matcher(responseMsg);
        StringBuffer sb = new StringBuffer();
        Calendar c = Calendar.getInstance();

        while (m.find())
        {
            String foundText = responseMsg.substring(m.start(), m.end());
            Date dt = new Date();
            try
            {
                if (!m.group(m.groupCount() - 1).isEmpty())
                {
//                    Date d = format1.parse(foundText);
                    c.setTime(dt);
                    c.add(Calendar.DATE, 1);
                    dt = c.getTime();
                    dt = new Date(dt.getTime());

                    m.appendReplacement(sb, format1.format(dt).toUpperCase());
                }
                else
                {
//                    Date d = format2.parse(foundText);
                    c.setTime(dt);
                    c.add(Calendar.DATE, 1);
                    dt = c.getTime();
                    dt = new Date(dt.getTime());
                    m.appendReplacement(sb, format2.format(dt).toUpperCase());
                }
            }
            catch (Exception e)
            {
                LOG.error("Can't convert string: " + foundText + " to date!", e);
                m.appendReplacement(sb, foundText);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
