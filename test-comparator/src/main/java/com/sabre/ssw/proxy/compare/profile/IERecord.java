/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.compare.profile;

public class IERecord
{
private String file;
private boolean regex;
private boolean command;
public IERecord(String file, boolean regex, boolean command)
{
    super();
    this.setCommand(command);
    this.file = file;
    this.regex = regex;
}
public String getFile()
{
    return file;
}
public void setFile(String file)
{
    this.file = file;
}
public boolean isRegex()
{
    return regex;
}
public void setRegex(boolean regex)
{
    this.regex = regex;
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
