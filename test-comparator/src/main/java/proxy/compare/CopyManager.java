/* Copyright 2009 EB2 International Limited */
package proxy.compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyManager
{
    public static boolean copyFilesInDirs(File sourceDir, File targetDir)
    {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if (!sourceDir.isDirectory() || !targetDir.isDirectory())
        {
            return false;
        }
        File[] fileList = targetDir.listFiles();
        for (File f : fileList)
        {
            if (!f.isDirectory())
                f.delete();

        }

        for (File f : sourceDir.listFiles())
        {
            if (!f.isDirectory() && !copyfile(f, new File(targetDir.getPath() + File.separator + f.getName())))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean copyfile(File srFile, File dtFile)
    {

        try
        {
            InputStream in = new FileInputStream(srFile);

            OutputStream out = new FileOutputStream(dtFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
