/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.compare.profile;

import java.util.ArrayList;
import java.util.List;

import com.sabre.ssw.proxy.compare.profile.IERecord;

public class Profile
{
    private String name;
    private boolean skipNameSpace = true;
    private boolean contentCompare = true;
    private ArrayList<TagRecord> tagList = new ArrayList<TagRecord>();
    private ArrayList<IERecord> includeList = new ArrayList<IERecord>();
    private ArrayList<IERecord> excludeList = new ArrayList<IERecord>();

    public void setTagList(ArrayList<TagRecord> tagList)
    {
        this.tagList = tagList;
    }

    public Profile(String name)
    {
        this.name = name;
        includeList.add(new IERecord("*", false, false));
        excludeList.add(new IERecord("*.zhd", false, false));
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addTag(TagRecord tagRecord)
    {
        tagList.add(tagRecord);
    }

    public boolean removeTag(TagRecord tagRecord)
    {
        return tagList.remove(tagRecord);
    }

    public List<TagRecord> getTagList()
    {
        return tagList;
    }

    public boolean isSkipNameSpace()
    {
        return skipNameSpace;
    }

    public void setSkipNameSpace(boolean skipNameSpace)
    {
        this.skipNameSpace = skipNameSpace;
    }

    public ArrayList<IERecord> getIncludeList()
    {
        return includeList;
    }

    public void setIncludeList(ArrayList<IERecord> includeList)
    {
        this.includeList = includeList;
    }

    public ArrayList<IERecord> getExcludeList()
    {
        return excludeList;
    }

    public void setExcludeList(ArrayList<IERecord> excludeList)
    {
        this.excludeList = excludeList;
    }

    public void addInclude(IERecord ieRecord)
    {
        includeList.add(ieRecord);
    }

    public boolean removeInclude(IERecord ieRecord)
    {
        boolean result = includeList.remove(ieRecord);
        if (includeList.size() == 0)
        {
            includeList.add(new IERecord("*", false, false));
        }
        return result;
    }

    public void addExclude(IERecord ieRecord)
    {
        excludeList.add(ieRecord);
    }

    public boolean removeExclude(IERecord ieRecord)
    {
        return excludeList.remove(ieRecord);
    }

    public boolean isContentCompare()
    {
        return contentCompare;
    }

    public void setContentCompare(boolean contentCompare)
    {
        this.contentCompare = contentCompare;
    }

     @Override
     public String toString()
     {
     return name;
     }

}
