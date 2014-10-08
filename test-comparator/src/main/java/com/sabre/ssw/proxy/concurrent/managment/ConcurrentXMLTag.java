/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.concurrent.managment;

public class ConcurrentXMLTag
{
    private String tag;
    private String attributes;

    public ConcurrentXMLTag()
    {
    }

    public ConcurrentXMLTag(String tag, String attributes)
    {
        this.tag = tag;
        this.attributes = attributes;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getAttributes()
    {
        return attributes;
    }

    public void setAttributes(String attributes)
    {
        this.attributes = attributes;
    }

}
