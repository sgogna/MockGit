/* Copyright 2009 EB2 International Limited */
package filehandlers;

import java.util.Comparator;

public class FileNamePairComparator implements Comparator<FileNamePair>
{
    @Override
    public int compare(FileNamePair o1, FileNamePair o2)
    {
        return o1.getName().compareTo(o2.getName());
    }
}