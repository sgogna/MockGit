/* Copyright 2009 EB2 International Limited */
package com.sabre.sabresonic.mockserver.core.sessionsinfo;

import com.sabre.sabresonic.mockserver.core.service.beans.PassengerName;

import java.util.HashMap;
import java.util.Map;


public class SessionInfo
{
    private Map<String, Integer> counterMap = new HashMap<String, Integer>();
    private String suffix = "";
    private String createDate = "";
    private String path = "";
    private long timeDiff = 0;
    private PassengerName passengerName;
    private String[] originDestinations;
    private boolean exchangeStarted = false;


    public int getNumber(String request)
    {
        if (counterMap.containsKey(request))
        {
            counterMap.put(request, counterMap.get(request) + 1);
        }
        else
        {
            counterMap.put(request, 1);
        }
        return counterMap.get(request);
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    public String getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(String createDate)
    {
        this.createDate = createDate;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public long getTimeDiff()
    {
        return timeDiff;
    }

    public void setTimeDiff(long timeDiff)
    {
        this.timeDiff = timeDiff;
    }

    public void setPassengerName(PassengerName passengerName) {
        this.passengerName = passengerName;
    }

    public PassengerName getPassengerName() {
        return passengerName;
    }

    public String[] getOriginDestinations() {
        return originDestinations;
    }

    public void setOriginDestinations(String[] originDestinations) {
        this.originDestinations = originDestinations;
    }

    public boolean getExchangeStarted()
    {
        return exchangeStarted;
    }

    public void setExchangeStarted(boolean exchangeStarted)
    {
        this.exchangeStarted = exchangeStarted;
    }

}
