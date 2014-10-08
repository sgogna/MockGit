/* Copyright 2009 EB2 International Limited */
package com.sabre.ssw.proxy.compare.profile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class ProfileFilesManager {

    private String savePath;
    XStream streamer = new XStream();

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public boolean saveAll(List<Profile> profileList) {
        File dir = new File(savePath);
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".prof")) {
                f.delete();
            }
        }
        // profileList = profileList2;
        for (Profile p : profileList) {

            File f = new File(savePath + File.separator + p.getName() + ".prof");
            try {
                streamer.toXML(p, new FileWriter(f));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    public List<Profile> readProfiles() {
        List<Profile> profiles = new ArrayList<Profile>();
        File dir = new File(savePath);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(".prof")) {
                    Profile p = readProfileFormFile(f);
                    if (p != null) {
                        profiles.add(p);
                    }
                }
            }
        }
        return profiles;

    }

    private Profile readProfileFormFile(File f) {
        try {
            Profile p = (Profile) streamer.fromXML(f);
            return p;
        } catch (Exception e) {
            return null;
        }
    }

}
