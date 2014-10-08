/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.compare.profile;

public class TagRecord
{
    private boolean command;
    private String file;
    private String tag;
    private String param;

    private boolean fileCheckBox = false;
    private boolean tagCheckBox = false;
    private boolean attributeCheckBox = false;

    public TagRecord(String file, boolean command, String tag, String param, boolean fileCheckBox, boolean tagCheckBox, boolean attributeCheckBox)
    {
        super();
        this.command = command;
        this.file = file;
        this.tag = tag;
        this.param = param;
        this.fileCheckBox = fileCheckBox;
        this.tagCheckBox = tagCheckBox;
        this.attributeCheckBox = attributeCheckBox;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getAttribute()
    {
        return param;
    }

    public void setAttribute(String param)
    {
        this.param = param;
    }

    public void setFileCheckBoxValue(boolean value)
    {
        this.fileCheckBox = value;
    }

    public boolean getFileCheckBoxValue()
    {
        return fileCheckBox;
    }

    public void setTagCheckBoxValue(boolean value)
    {
        this.tagCheckBox = value;
    }

    public boolean getTagCheckBoxValue()
    {
        return tagCheckBox;
    }

    public void setAttributeCheckBoxValue(boolean value)
    {
        this.attributeCheckBox = value;
    }

    public boolean getAttributeCheckBoxValue()
    {
        return attributeCheckBox;
    }

    public boolean isCommand()
    {
        return command;
    }

    public void setCommand(boolean command)
    {
        this.command = command;
    }
    
}
