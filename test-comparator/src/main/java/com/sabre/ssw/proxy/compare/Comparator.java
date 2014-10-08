/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import util.Pair;
import util.XmlComparator;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.sabre.ssw.proxy.compare.profile.TagRecord;

public class Comparator
{
    private XmlComparator xmlComparator = new XmlComparator();

    public void setProfile(Profile profile, String fileName)
    {
        // TODO
        // check if correct
        List<String> skippedTags = new LinkedList<String>();
        // String[0] - tag
        // String[1] - attribute
        List<String[]> skippedAttributes = new LinkedList<String[]>();
        
        if (profile == null)
        {
            xmlComparator.setSkippedTags(skippedTags);
            xmlComparator.setSkippedAttributes(skippedAttributes);
            xmlComparator.setSkipNamespace(false);
            return;
        }
        
        List<TagRecord> tagRecords = profile.getTagList();
        for (Iterator iterator = tagRecords.iterator(); iterator.hasNext();)
        {
            TagRecord t = (TagRecord) iterator.next();
            if (checkFileMatches(t, fileName))
            {
                String tag = t.getTagCheckBoxValue() ? t.getTag() : t.getTag().replaceAll("\\?", ".?").replaceAll("\\*", ".*?");
                String attribute = t.getAttributeCheckBoxValue() ? t.getAttribute() : t.getAttribute().replaceAll("\\?", ".?").replaceAll("\\*", ".*?");
                if (attribute.equals(".*?"))
                {
                    skippedTags.add(tag);
                }
                else
                {
                    // String[0] - tag
                    // String[1] - attribute
                    skippedAttributes.add(new String[] { tag, attribute });
                }
            }
        }
        xmlComparator.setSkippedTags(skippedTags);
        xmlComparator.setSkippedAttributes(skippedAttributes);
        xmlComparator.setSkipNamespace(profile.isSkipNameSpace());
    }

    public boolean isEqualsFiles(File file1, File file2, Profile profile)
    {
        if (profile != null && !profile.isContentCompare()) {
            return true;
        }
        setProfile(profile, file1.getName());
        return compareFiles(file1, file2);
    }
    
    public Pair<Document> compareFilesWithDiffSelection(File file1, File file2, Profile profile) 
    {
        setProfile(profile, file1.getName());
        return xmlComparator.compareXmlFilesWithDiffSelection(file1, file2);
    }
    
    public boolean isXmlFile(File file) {
        return file.getName().matches(".*\\.xml");
    }

    private boolean compareFiles(File file1, File file2)
    {
        boolean result = false;
        if (isXmlFile(file1))
        {
            result = compareXmlFiles(file1, file2);
        }
        else
        {
            result = compareXhdFiles(file1, file2);
        }
        return result;
    }

    private boolean compareXhdFiles(File file1, File file2)
    {
        boolean result = false;
        String fileContent1 = null;
        String fileContent2 = null;
        try
        {
            // TODO
            // after changing xhd format to properties file, compare properties
            // not
            // content
            fileContent1 = readFile(file1);
            fileContent2 = readFile(file2);
            result = fileContent1.equals(fileContent2);
        }
        catch (IOException e)
        {
            // TODO
            // implement error handling
            result = false;
        }
        return result;
    }

    private boolean compareXmlFiles(File file1, File file2)
    {
        boolean result = false;
        try
        {
            result = xmlComparator.compareXmlFiles(file1, file2);
        }
        catch (SAXException e)
        {
            // e.printStackTrace();
            // TODO
            // implement error handling
            // check situation with two empty files
            try
            {
                if (readFile(file1).equals(readFile(file2)))
                {
                    result = true;
                }
                else
                {
                    result = false;
                }
            }
            catch (IOException e1)
            {
                result = false;
            }
        }
        return result;
    }

    private String readFile(File file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null)
        {
            sb.append(line);
        }
        return sb.toString();
    }

    private boolean checkFileMatches(TagRecord record, String file)
    {
        String value = record.getFileCheckBoxValue() ? record.getFile() : record.getFile().replaceAll("\\?", ".?").replaceAll("\\*", ".*?");
        if (!value.equals(".*?"))
        {
            value = record.isCommand() ? ".*?" + value : value;
        }
        return (value.equals(".*?") || file.matches(value));
    }

}
