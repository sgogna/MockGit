/* Copyright 2009 EB2 International Limited */
package com.sabre.sabresonic.mockserver.core.message.replacers.patterns;


public interface DatePattern
{

    public String replaceDates(long difftime, String responseMsg);
    public String replaceDatesWithCurrentDate(String responseMsg);

}
