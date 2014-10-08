/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.concurrent.managment;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentRequestRule
{
    private String name = "";
    private ConcurrentRequestType type = ConcurrentRequestType.XML;
    private String filePattern = "";
    private List<ConcurrentHeaderAttribute> includeHeaderAttributes = new ArrayList<ConcurrentHeaderAttribute>();
    private List<ConcurrentHeaderAttribute> excludeHeaderAttributes = new ArrayList<ConcurrentHeaderAttribute>();
    private List<ConcurrentXMLTag> xmlExcludes = new ArrayList<ConcurrentXMLTag>();

    public ConcurrentRequestRule()
    {
        includeHeaderAttributes.add(new ConcurrentHeaderAttribute("*"));
    }

    public List<ConcurrentHeaderAttribute> getExcludeHeaderAttributes()
    {
        return excludeHeaderAttributes;
    }

    public String getFilePattern()
    {
        return filePattern;
    }

    public List<ConcurrentHeaderAttribute> getIncludeHeaderAttributes()
    {
        return includeHeaderAttributes;
    }

    public String getName()
    {
        return name;
    }

    public ConcurrentRequestType getType()
    {
        return type;
    }

    public List<ConcurrentXMLTag> getXmlExcludes()
    {
        return xmlExcludes;
    }

    public void setExcludeHeaderAttributes(List<ConcurrentHeaderAttribute> excludeHeaderAttributes)
    {
        xmlExcludes.clear();
        this.excludeHeaderAttributes = excludeHeaderAttributes;
    }

    public void setFilePattern(String filePattern)
    {
        this.filePattern = filePattern;
    }

    public void setIncludeHeaderAttributes(List<ConcurrentHeaderAttribute> includeHeaderAttributes)
    {
        xmlExcludes.clear();
        if (includeHeaderAttributes.isEmpty())
        {
            includeHeaderAttributes.add(new ConcurrentHeaderAttribute("*"));
        }
        this.includeHeaderAttributes = includeHeaderAttributes;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setType(ConcurrentRequestType type)
    {
        this.type = type;
    }

    public void setXmlExcludes(List<ConcurrentXMLTag> xmlExcludes)
    {
        includeHeaderAttributes.clear();
        excludeHeaderAttributes.clear();
        this.xmlExcludes = xmlExcludes;
    }

    @Override
    public String toString()
    {
        return getName();
    }

}
