/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.concurrent.managment;

public class ConcurrentHeaderAttribute
{
    private String attribute;

    public ConcurrentHeaderAttribute(String attribute)
    {
        super();
        this.attribute = attribute;
    }

    public ConcurrentHeaderAttribute()
    {
    }

    public String getAttribute()
    {
        return attribute;
    }

    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }
}
