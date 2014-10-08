/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.concurrent.fileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.sabre.ssw.proxy.concurrent.managment.ConcurrentRequestRule;
import com.thoughtworks.xstream.XStream;

public class ConcurrentRequestsRuleFilesManager {

    private static final String FILE_EXTENSION = ".crr";
    private String savePath;
    XStream streamer = new XStream();

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }


    public boolean saveAll(List<ConcurrentRequestRule> ruleList) {
        File dir = new File(savePath);
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(FILE_EXTENSION)) {
                f.delete();

            }
        }
        for (ConcurrentRequestRule rule : ruleList) {

            File f = new File(savePath + File.separator + rule.getName() + FILE_EXTENSION);
            try {
                OutputStream outputStream = new FileOutputStream(f);
                streamer.toXML(rule, outputStream);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    public List<ConcurrentRequestRule> readRules() {
        List<ConcurrentRequestRule> rules = new ArrayList<ConcurrentRequestRule>();
        File dir = new File(savePath);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(FILE_EXTENSION)) {
                    ConcurrentRequestRule rule = readRuleFormFile(f);
                    if (rule != null) {
                        rules.add(rule);
                    }
                }
            }
        }
        return rules;

    }

    private ConcurrentRequestRule readRuleFormFile(File f) {
        try {
            InputStream inputStream = new FileInputStream(f);
            ConcurrentRequestRule rule = (ConcurrentRequestRule) streamer.fromXML(inputStream);
            inputStream.close();
            return rule;
        } catch (Exception e) {
            return null;
        }
    }

}
