/* Copyright 2009 EB2 International Limited */
package helpers;

import java.io.File;

public class TableFile extends File
{
    private static final long serialVersionUID = 1L;

    public TableFile(String pathname)
    {
        super(pathname);
    }

    @Override
    public String toString()
    {
        return this.getName();
    }

}
