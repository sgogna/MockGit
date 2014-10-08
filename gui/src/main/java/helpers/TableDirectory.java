/* Copyright 2009 EB2 International Limited */
package helpers;

import java.io.File;

public class TableDirectory extends File
{

    private static final long serialVersionUID = 1L;

    public TableDirectory(String pathname)
    {
        super(pathname);
    }

    @Override
    public String toString()
    {
        return this.getParentFile().getName() + File.separator
                + this.getName();
    }

}
