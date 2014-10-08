/* Copyright 2009 EB2 International Limited */
package filehandlers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sabre.ssw.proxy.compare.profile.IERecord;

import fileManagers.MainFileManager;

public class FilesTagsManager
{
    private List<String> tgList = new ArrayList<String>();
    private MainFileManager fileManager;
    private FileNamePairComparator fileNamePairComparator = new FileNamePairComparator();

    public MainFileManager getFileManager()
    {
        return fileManager;
    }

    public void setFileManager(MainFileManager fileManager)
    {
        this.fileManager = fileManager;
    }

    public List<FileNamePair> getListFiles(File f, List<IERecord> include, List<IERecord> exclude)
    {
        if (fileManager.isTestDir(f))
        {
            return convertToFileNamePair(fileManager.getFilesForTest(f, include, exclude));
        }
        return convertToFileNamePair(fileManager.getFilesForAll(f, include, exclude));
    }

    public List<FileNamePair> getFiles(File f, List<IERecord> include, List<IERecord> exclude, boolean noNumber)
    {
        List<FileNamePair> list = getListFiles(f, include, exclude);
        if (noNumber)
        {
            List<String> tempName = new ArrayList<String>();
            List<FileNamePair> valueList = new ArrayList<FileNamePair>();
            for (FileNamePair fileNamePair : list)
            {
                String name = fileNamePair.getName();
                if (!name.contains("UNKNOWN"))
                {
                    String end = name.substring(name.length() - 6, name.length());
                    name = name.replaceFirst("_\\d*?_" + end, "_*_" + end);
                }
                if (!tempName.contains(name))
                {
                    tempName.add(name);
                    valueList.add(new FileNamePair(name, fileNamePair.getFile()));
                }
            }
            list = valueList;
        }
        list.add(new FileNamePair("*", null));
        Collections.sort(list, fileNamePairComparator);
        return list;
    }

    private List<FileNamePair> convertToFileNamePair(List<File> files)
    {
        List<FileNamePair> list = new ArrayList<FileNamePair>();
        for (File f : files)
        {
            list.add(new FileNamePair(f.getName(), f));
        }
        return list;
    }

    public List<FileNamePair> getCommands(File f, List<IERecord> include, List<IERecord> exclude)
    {
        List<FileNamePair> templist = getListFiles(f, include, exclude);
        List<FileNamePair> list = new ArrayList<FileNamePair>();
        List<String> nameList = new ArrayList<String>();
        for (FileNamePair fileNamePair : templist)
        {
            String name = fileNamePair.getName();
            if (!name.contains("UNKNOWN"))
            {
                String end = name.substring(name.length() - 6, name.length());
                name = name.replaceFirst("_\\d*?_" + end, "_*_" + end).replaceFirst(".*?__", "");
            }
            if (!nameList.contains(name))
            {
                nameList.add(name);
                list.add(new FileNamePair(name, fileNamePair.getFile()));
            }
        }
        list.add(new FileNamePair("*", null));
        Collections.sort(list, fileNamePairComparator);
        return list;
    }

    public Map<String, String> getTags(List<FileNamePair> fileList, String filename, boolean regex, boolean command, boolean skipNamespace)
    {

        String RegexFilename = (command ? ".*?" : "") + (regex ? filename : filename.replaceAll("\\?", ".?").replaceAll("\\*", ".*?"));
        Map<String, String> map = new TreeMap<String, String>();
        if (filename.equals("*"))
        {
            for (FileNamePair fileNamePair : fileList)
            {
                if (fileNamePair.getFile() != null)
                {
                    map.putAll(getTagList(fileNamePair.getFile(), skipNamespace));
                }
            }
        }
        else
        {
            for (FileNamePair fileNamePair : fileList)
            {
                if (fileNamePair.getFile() != null && fileNamePair.getFile().getName().matches(RegexFilename))
                {
                    map.putAll(getTagList(fileNamePair.getFile(), skipNamespace));
                    if (command)
                    {
                        break;
                    }
                }
            }

        }

        return map;
    }

    public Map<String, String> getTags(File f)
    {
        Map<String, String> tagList = new TreeMap<String, String>();
        tagList.put("*", null);
        try
        {
            FileInputStream fstream = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String text = "";
            String strLine;
            while ((strLine = br.readLine()) != null)
            {
                text += strLine;

            }
            Pattern p = Pattern.compile("<(\\p{Alpha}[^\\s/>]*)(>|\\p{Blank}.*?>|/>)");
            Matcher m = p.matcher(text);
            while (m.find())
            {
                String tag = text.substring(m.start(1), m.end(1));
                if (!tagList.containsKey(tag))
                {
                    tagList.put(tag, text.substring(m.start(), m.end()));
                }
            }

        }
        catch (IOException e)
        {
        }
        return tagList;
    }

    public List<String> getAtributes(String tag)
    {
        ArrayList<String> attrList = new ArrayList<String>();
        attrList.add("*");
        for (String tag1 : tgList)
        {
            if (tag.equals("*") || tag1.matches("<.*?:?" + tag + "(>|\\p{Blank}.*?>|/>)"))
            {
                Pattern p = Pattern.compile("(\\s+((\\p{Alpha}[^\\s/>]*){1})=\"[^\"]+\")");
                Matcher m = p.matcher(tag1);
                while (m.find())
                {
                    String param = tag1.substring(m.start(2), m.end(2));
                    if (!attrList.contains(param))
                    {
                        attrList.add(param);
                    }
                }
                if (!tag.equals("*"))
                {
                    Collections.sort(attrList);
                    return attrList;
                }
            }
        }
        Collections.sort(attrList);
        return attrList;
    }

    private Set<String> getAttributeFormTag(String tagText)
    {
        Set<String> attrSet = new HashSet<String>();
        if (tagText == null)
        {
            return attrSet;
        }
        Pattern p = Pattern.compile("(\\s+((\\p{Alpha}[^\\s/>]*){1})=\"[^\"]+\")");
        Matcher m = p.matcher(tagText);
        while (m.find())
        {
            attrSet.add(tagText.substring(m.start(2), m.end(2)));
        }
        return attrSet;
    }

    public List<String> getAttributes(Map<String, String> tagMap, String tag, boolean regex)
    {

        String regexTag = (regex ? tag : tag.replaceAll("\\?", ".?").replaceAll("\\*", ".*?"));
        Set<String> attrSet = new HashSet<String>();
        if (tag.equals("*"))
        {
            for (String key : tagMap.keySet())
            {
                attrSet.addAll(getAttributeFormTag(tagMap.get(key)));
            }
        }
        else
        {
            for (String key : tagMap.keySet())
            {
                if (key.matches(regexTag))
                {
                    attrSet.addAll(getAttributeFormTag(tagMap.get(key)));
                }
            }
        }
        ArrayList<String> attrList = new ArrayList<String>(attrSet);
        attrList.add("*");
        Collections.sort(attrList);
        return attrList;
    }

    public Map<String, String> getTagList(File file, boolean isSkipNameSpace)
    {
        Map<String, String> map = new TreeMap<String, String>();
        Map<String, String> tempMap = getTags(file);
        String value = "";
        for (String key : tempMap.keySet())
        {
            value = tempMap.get(key);
            if (isSkipNameSpace && key.contains(":"))
            {
                key = key.split(":")[1];
            }
            map.put(key, value);
        }
        return map;
    }

    public List<FileNamePair> getAllFiles()
    {
        List<FileNamePair> list = new ArrayList<FileNamePair>();
        List<String> nameList = new ArrayList<String>();
        for (File file : fileManager.getFilesFromAllBases())
        {

            String name = file.getName();
            String end = name.substring(name.length() - 6, name.length());
            name = name.replaceFirst("_\\d*?_" + end, "*");
            if (!nameList.contains(name))
            {
                nameList.add(name);
                list.add(new FileNamePair(name, file));
            }
        }
        Collections.sort(list, fileNamePairComparator);
        return list;
    }

    public static List<String> getOriginalHostList(File f)
    {
        Properties headers = new Properties();
        MainFileManager.readHeaderFile(new File(f.getAbsolutePath().replaceFirst(MainFileManager.XML_FILE_EXTENSION, MainFileManager.HEADER_FILE_EXTENSION)), headers);
        return MainFileManager.getHeaderOriginalHostAttrList(headers);
    }

}
