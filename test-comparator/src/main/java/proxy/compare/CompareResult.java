/* Copyright 2009 EB2 International Limited */
package proxy.compare;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompareResult
{

    private CompareStatus status;

    private List<File> newFilesInFirstDir = new ArrayList<File>();
    private List<File> newFilesInSecondDir = new ArrayList<File>();
    private List<File> errorFilesInFirstDir = new ArrayList<File>();
    private List<File> errorFilesInSecondDir = new ArrayList<File>();
    private List<File> equalFilesFromFirstDir = new ArrayList<File>();

    public CompareResult(CompareStatus status, List<File> newFilesInFirstDir, List<File> newFilesInSecondDir, List<File> errorFilesInFirstDir, List<File> errorFilesInSecondDir, List<File> equalFiles)
    {

        this.status = status;
        this.newFilesInFirstDir = newFilesInFirstDir;
        this.newFilesInSecondDir = newFilesInSecondDir;
        this.errorFilesInFirstDir = errorFilesInFirstDir;
        this.errorFilesInSecondDir = errorFilesInSecondDir;
        this.equalFilesFromFirstDir = equalFiles;
    }

    public CompareStatus getStatus()
    {
        return status;
    }

    public List<File> getNewFilesInFirstDir()
    {
        Collections.sort(newFilesInFirstDir);
        return newFilesInFirstDir;
    }

    public List<File> getNewFilesInSecondDir()
    {
        Collections.sort(newFilesInSecondDir);
        return newFilesInSecondDir;
    }

    public List<File> getErrorFilesInFirstDir()
    {
        return errorFilesInFirstDir;
    }

    public List<File> getErrorFilesInSecondDir()
    {
        return errorFilesInSecondDir;
    }

    @Override
    public String toString()
    {
        String s = "--------------------------\n";
        s += "COMPARE RESULT: " + status + "\n";

        if (errorFilesInFirstDir.size() > 0)
        {
            s += "\nERRORS:\n";
            if (errorFilesInFirstDir.size() > 0)
            {
                s += "ERRORS IN RECORD FOLDER: \n";
                for (File txt : errorFilesInFirstDir)
                {
                    s += txt.getName() + "\n";
                }
            }

        }

        if (newFilesInFirstDir.size() > 0 || newFilesInSecondDir.size() > 0)
        {
            s += "\nDIFFERENCE: \n";
            if (newFilesInFirstDir.size() > 0)
            {
                s += "NEW FILES: \n";
                for (File txt : newFilesInFirstDir)
                {
                    s += txt.getName() + "\n";
                }
            }
            if (newFilesInSecondDir.size() > 0)
            {
                s += "MISSED FILES: \n";
                for (File txt : newFilesInSecondDir)
                {
                    s += txt.getName() + "\n";
                }
            }
        }

        s += "END \n--------------------------";
        return s;
    }

    public List<File> getEqualFilesFromFirstDir()
    {
        Collections.sort(equalFilesFromFirstDir);
        return equalFilesFromFirstDir;
    }

}
