/* Copyright 2009 EB2 International Limited */
package proxy.compare;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.sabre.ssw.proxy.compare.profile.IERecord;

import fileManagers.InfoFileManager;

public class FolderComparator
{
    public static final String NAME = "Compare dirs";
    private static FilenameFilter filter = new FilenameFilter()
    {

        @Override
        public boolean accept(File dir, String name)
        {
            if (name.startsWith("."))
            {
                return false;
            }
            return true;
        }
    };

    public List<CompareResult> compareAllFolders(String path)
    {
        ArrayList<CompareResult> result = new ArrayList<CompareResult>();

        return result;
    }

    public CompareResult compareDirs(String path, String path_comp)
    {
        return compareDirs(path, path_comp, null, null);
    }

    public CompareResult compareDirs(String path, String path_comp, List<IERecord> includeFiles, List<IERecord> excludeFiles)
    {
        File dir = new File(path);
        File dir_comp = new File(path_comp);
        if (!dir.exists() || !dir_comp.exists() || !dir.isDirectory()
                || !dir_comp.isDirectory())
        {
            return null;
        }

        File[] dirFileList = getFileList(dir, includeFiles, excludeFiles);
        File[] compareFileList = getFileList(dir_comp, includeFiles, excludeFiles);

        List<File> newFilesInSecDir = getDifferenceFiles(compareFileList,
                dirFileList);
        List<File> newFilesInFirstDir = getDifferenceFiles(dirFileList,
                compareFileList);
        List<File> errorListInFirstDir = getErrors(dir, dirFileList);
        List<File> errorListInSecDir = getErrors(dir_comp, compareFileList);

        List<File> equalFiles = getEqualFiles(Arrays.asList(dirFileList), newFilesInFirstDir);

        CompareStatus status = CompareStatus.Equals;
        if (newFilesInFirstDir.size() > 0 || newFilesInSecDir.size() > 0)
        {
            status = CompareStatus.Different;
        }

        return new CompareResult(status, newFilesInFirstDir, newFilesInSecDir, errorListInFirstDir, errorListInSecDir, equalFiles);
    }

    public static File[] getFileList(File dir, List<IERecord> include, List<IERecord> exclude)
    {

        if (include == null || exclude == null)
        {
            return dir.listFiles(filter);
        }

        List<File> fileList = new ArrayList<File>();
        for (File f : dir.listFiles(filter))
        {
            if (isOnList(f.getName(), include))
            {
                fileList.add(f);
            }
        }

        ListIterator<File> iterator = fileList.listIterator();
        while (iterator.hasNext())
        {
            File f = iterator.next();
            if (isOnList(f.getName(), exclude))
            {
                iterator.remove();
            }
        }

        return (File[]) fileList.toArray(new File[0]);
    }

    private static boolean isOnList(String fileName, List<IERecord> list)
    {
        for (IERecord ieRecord : list)
        {
            String regex = (ieRecord.isCommand() ? ".*?" : "") + (ieRecord.isRegex() ? ieRecord.getFile() : ieRecord.getFile().replaceAll("\\?", ".?").replaceAll("\\*", ".*?"));
            if (fileName.matches(regex))
            {
                return true;
            }
        }
        return false;
    }

    private List<File> getEqualFiles(List<File> fileNameList,
            List<File> differentFiles1)
    {

        List<File> tmp = new ArrayList<File>(fileNameList);

        for (File file : differentFiles1)
        {
            tmp.remove(file);
        }
        return tmp;
    }

    private List<File> getErrors(File file, File[] dirFileList)
    {
        List<File> fileList = new ArrayList<File>();
        String errorlist = InfoFileManager.getInfo(file, InfoFileManager.FILES_WITH_ERRORS);
        if (errorlist != null)
        {

            List<String> fileNameList = Arrays.asList(errorlist.split(";"));
            for (File f : dirFileList)
            {
                if (fileNameList.contains(f.getName()))
                {
                    fileList.add(f);
                }
            }

        }
        return fileList;
    }

    private List<File> getDifferenceFiles(File[] fileList, File[] fileToRemove)
    {
        Map<String, File> list = new HashMap<String, File>();
        for (File f : fileList)
        {
            list.put(f.getName(), f);
        }
        for (File f : fileToRemove)
        {
            list.remove(f.getName());
        }
        List<File> files = new ArrayList<File>();
        Iterator<String> it = list.keySet().iterator();
        while (it.hasNext())
        {
            files.add(list.get(it.next()));
        }
        return files;
    }

}
