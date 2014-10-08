/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.concurrent.fileManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.XmlComparator;

import com.sabre.ssw.proxy.concurrent.managment.ConcurrentHeaderAttribute;
import com.sabre.ssw.proxy.concurrent.managment.ConcurrentRequestRule;

import fileManagers.MainFileManager;

public class ConcurrentRequestsManager
{
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentRequestsManager.class);
    private ConcurrentRequestsRuleFilesManager requestsRuleFilesManager = null;
    private static List<ConcurrentRequestRule> rulesList;

    public ConcurrentRequestsRuleFilesManager getRequestsRuleFilesManager()
    {
        return requestsRuleFilesManager;
    }

    public void setRequestsRuleFilesManager(ConcurrentRequestsRuleFilesManager requestsRuleFilesManager)
    {
        this.requestsRuleFilesManager = requestsRuleFilesManager;
    }

    public void readRules()
    {
        rulesList = requestsRuleFilesManager.readRules();
    }

    public static boolean isRuleFor(String filename)
    {
        return getRule(filename) != null;
    }

    private static ConcurrentRequestRule getRule(String filename)
    {
        for (ConcurrentRequestRule rule : rulesList)
        {
            if (filename.matches(rule.getFilePattern().replaceAll("\\?", ".?").replaceAll("\\*", ".*?")))
            {
                return rule;
            }
        }
        return null;
    }

    public static String computeHash(String filename, String text, Properties header)
    {
        ConcurrentRequestRule rule = getRule(filename);
        String textWithSkippedElements = "";
        switch (rule.getType())
        {
            case HEADER:
                textWithSkippedElements = skipHeader(header, rule);
                break;
            case XML:
                textWithSkippedElements = skipXml(text, rule);
                break;
        }
        MessageDigest digest = null;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            LOG.error("No such SHA-256 hash algorithm. ", e);
            return "";
        }
        return (new BigInteger(1, digest.digest(textWithSkippedElements.getBytes()))).toString(16);

    }

    private static String skipXml(String text, ConcurrentRequestRule rule)
    {
        String s = XmlComparator.getXMLWithSkippedTags(text, rule.getXmlExcludes());
        return s;
    }

    private static String skipHeader(Properties header, ConcurrentRequestRule rule)
    {
        String oryginalHost = MainFileManager.getHeaderOriginalHostString(header);
        if (oryginalHost != null && oryginalHost.contains("?"))
        {
            String[] array = oryginalHost.split("\\?");
            return array[0] + "?" + excludeIncludeAttr(array[1], rule);
        }
        else
        {
            return oryginalHost;
        }
    }

    private static String excludeIncludeAttr(String attributeStirng, ConcurrentRequestRule rule)
    {
        Properties properties = new Properties();
        Properties temp = MainFileManager.getHeaderOriginalHostAttrProperties(attributeStirng);
        for (String key : temp.stringPropertyNames())
        {
            if (isOnList(key, rule.getIncludeHeaderAttributes()))
            {
                properties.setProperty(key, temp.getProperty(key));
            }
        }

        for (Object key : properties.keySet())
        {
            if (isOnList((String) key, rule.getExcludeHeaderAttributes()))
            {
                properties.remove(key);
            }
        }

        String returnString = "";
        for (String s : properties.stringPropertyNames())
        {
            returnString += s + "=" + properties.getProperty(s);
        }

        return returnString;
    }

    private static boolean isOnList(String headerAttribute, List<ConcurrentHeaderAttribute> list)
    {
        for (ConcurrentHeaderAttribute attr : list)
        {
            String regex = attr.getAttribute().replaceAll("\\?", ".?").replaceAll("\\*", ".*?");
            if (headerAttribute.matches(regex))
            {
                return true;
            }
        }
        return false;
    }

}
