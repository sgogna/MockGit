/* Copyright 2009 EB2 International Limited */
package helpers;

import java.io.File;

public class TableRow
{
    private int id;
    private File firstFile;
    private File secondFile;

    public TableRow(int id, File firstFile, File secondFile)
    {
        this.id = id;
        this.firstFile = firstFile;
        this.secondFile = secondFile;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public File getFirstFile()
    {
        return firstFile;
    }

    public void setFirstFile(File firstFile)
    {
        this.firstFile = firstFile;
    }

    public File getSecondFile()
    {
        return secondFile;
    }

    public void setSecondFile(File secondFile)
    {
        this.secondFile = secondFile;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TableRow other = (TableRow) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
