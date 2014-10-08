/* Copyright 2009 EB2 International Limited */
package fileManagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class InfoFileManager
{

    private static final Logger LOG = LoggerFactory.getLogger(InfoFileManager.class);
    private static final String INFO_FILE_NAME = ".info";
    private static final String ORDER_FILE_NAME = ".fileorder";
    private static final String CONCURRENT_FILE_NAME = ".conc";
    public static final String TEST_EXECUTION_STATUS = "correct";
    public static final String COMPARE_RESULT = "compareResult";
    public static final String FAILURE_REASON = "failureReason";
    public static final String COMPARE_ALGORITHM = "compareAlgorithm";
    public static final String SESSIONID = "sessionId";
    public static final String START_TEST_TIME = "startTestTime";
    public static final String FORMATTED_START_TEST_TIME = START_TEST_TIME + "Formatted";
    public static final String FILES_WITH_ERRORS = "errorsFiles";
    public static final String FILES_SEPARATOR = ";";
    private static XStream streamer = new XStream();

    public static boolean createInfoFile(String path)
    {
        File dir = new File(path);
        if (dir.exists())
        {
            boolean deleteAll = true;
            for (File f : dir.listFiles())
            {
                try
                {
                    f.delete();
                }
                catch (Exception e)
                {
                    LOG.error("Cannot delete file: " + f.getName());
                    deleteAll = false;
                }

            }
            if (!deleteAll)
            {
                return false;
            }
        }
        File infofile = new File(path + File.separator + INFO_FILE_NAME);
        if (!infofile.exists())
        {
            try
            {

                (new File(path)).mkdirs();
                return (infofile.createNewFile());
            }
            catch (IOException e)
            {
                LOG.error("Cannot create an info file. ", e);
                return false;
            }
        }
        else
        {

            try
            {
                OutputStream out = new FileOutputStream(infofile);
                out.close();
            }
            catch (Exception e)
            {
                LOG.error("Could not clear an info file. ", e);
                return false;
            }

        }

        return true;
    }

    public static File getInfoFile(String path)
    {
        File infofile = new File(path + File.separator + INFO_FILE_NAME);
        if (infofile.exists())
            return infofile;
        return null;
    }

    public synchronized static boolean addInfo(String path, String nameInfo, String info)
    {
        Properties p = getProperties(path);
        if (p == null)
        {
            return false;
        }
        p.put(nameInfo, info);

        return saveProperty(path, p);
    }

    public synchronized static boolean addFileToOrderList(String path, String fileName)
    {
        OrderFileInfo orderFileInfo = null;
        File f = new File(path + File.separator + ORDER_FILE_NAME);
        if (f.exists())
        {
            orderFileInfo = (OrderFileInfo) streamer.fromXML(f);
        }
        else
        {
            orderFileInfo = new OrderFileInfo();
        }
        orderFileInfo.addFile(fileName);
        try
        {
            FileWriter out = new FileWriter(f);
            streamer.toXML(orderFileInfo, out);
            out.close();
            return true;
        }
        catch (IOException e)
        {
            LOG.error("Can't write to orderFile", e);
        }
        return false;
    }

    public synchronized static boolean addFileToHashList(String path, String fileName, String hash)
    {
        Properties p = new Properties();
        File f = new File(path + File.separator + CONCURRENT_FILE_NAME);
        if (f.exists())
        {
            try
            {
                p.load(new FileReader(f));
            }
            catch (Exception e)
            {
                LOG.error("Can't read file " + f.toString(), e);
            }

        }

        p.put(hash, fileName);
        try
        {
            p.store(new FileWriter(f), "");
        }
        catch (IOException e)
        {
            LOG.error("Can't write to file" + f, e);
        }
        return false;
    }

    public synchronized static OrderFileInfo getOrderInfoFile(String path)
    {
        File f = new File(path + File.separator + ORDER_FILE_NAME);
        OrderFileInfo orderFileInfo = null;
        if (f.exists())
        {
            orderFileInfo = (OrderFileInfo) streamer.fromXML(f);
        }
        else
        {
            orderFileInfo = new OrderFileInfo();
        }
        return orderFileInfo;
    }

    public synchronized static String getInfo(String path, String nameInfo)
    {

        Properties p = getProperties(path);
        if (p == null)
        {
            return null;
        }
        return p.getProperty(nameInfo);

    }

    public synchronized static String getInfo(File testDir, String nameInfo)
    {
        File infoFile = new File(testDir.getPath() + File.separator + INFO_FILE_NAME);
        if (!infoFile.exists())
        {
            return null;
        }
        Properties p = getProperties(infoFile);
        if (p == null)
        {
            return null;
        }
        return p.getProperty(nameInfo);

    }

    private static Properties getProperties(String path)
    {
        File infoFile = new File(path + File.separator + INFO_FILE_NAME);
        if (!infoFile.exists())
        {
            createInfoFile(path);
        }
        return getProperties(infoFile);
    }

    private static Properties getProperties(File testFile)
    {

        Properties p = new Properties();
        FileInputStream in;
        try
        {
            in = new FileInputStream(testFile);
            p.load(in);
            in.close();
            return p;
        }
        catch (Exception e)
        {
            LOG.error("Error during read form file: " + testFile.getName(), e);
        }

        return null;

    }

    public synchronized static boolean setErrorFile(String path, String fileName)
    {
        Properties p = getProperties(path);
        if (p == null)
        {
            return false;
        }
        String errors = p.getProperty(FILES_WITH_ERRORS);
        if (errors == null)
        {
            p.put(FILES_WITH_ERRORS, fileName);
        }
        else
        {
            p.put(FILES_WITH_ERRORS, errors + FILES_SEPARATOR + fileName);
        }
        return saveProperty(path, p);
    }

    private static boolean saveProperty(String path, Properties p)
    {
        File infoFile = new File(path + File.separator + INFO_FILE_NAME);
        OutputStream out;
        try
        {
            out = new FileOutputStream(infoFile);
            p.store(out, "");
            out.close();
        }
        catch (Exception e)
        {
            LOG.error("Error during write to file: " + infoFile.getName(), e);
            return false;
        }

        return true;
    }

    public static String getHash(String path, String computeHash)
    {
        Properties p = new Properties();
        File f = new File(path + File.separator + CONCURRENT_FILE_NAME);
        if (f.exists())
        {
            try
            {
                p.load(new FileReader(f));
                return p.getProperty(computeHash);
            }
            catch (Exception e)
            {
                LOG.error("Can't read file " + f.toString(), e);
                return null;
            }

        }
        LOG.error("File " + f.getAbsolutePath() + " not exist!");
        return null;
    }
}
