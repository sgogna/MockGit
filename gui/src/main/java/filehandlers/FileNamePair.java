/* Copyright 2009 EB2 International Limited */
package filehandlers;

import java.io.File;

public class FileNamePair
{
    private String name;
    private File file;
    public FileNamePair(String name, File file)
    {
        super();
        this.name = name;
        this.file = file;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public File getFile()
    {
        return file;
    }
    public void setFile(File file)
    {
        this.file = file;
    }
    
    @Override
    public String toString()
    {
        return name;
    }

}
