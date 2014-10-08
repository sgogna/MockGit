/* Copyright 2009 EB2 International Limited */
package filehandlers;

import java.util.ArrayList;
import java.util.List;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.sabre.ssw.proxy.compare.profile.ProfileFilesManager;
import com.thoughtworks.xstream.XStream;

public class ProfileManager
{
    private String activeProfile = "";
    private Boolean equalSwitch = false;
    private XStream streamer = new XStream();
    private ProfileFilesManager profileFilesManager;

    private List<Profile> profileList = new ArrayList<Profile>();
    
    public Boolean isEqualSwitch()
    {
        return equalSwitch;
    }

    public void setEqualSwitch(Boolean equalSwitch)
    {
        this.equalSwitch = equalSwitch;
    }

    public List<Profile> getProfileList()
    {
        readProfiles();
        return profileList;
    }

    public String getActiveProfileName()
    {
        return activeProfile;
    }

    public Profile getActiveProfile()
    {
        return getProfile(activeProfile);
    }

    public void setActiveProfile(String activeProfile)
    {
        this.activeProfile = activeProfile;
    }

    public void addProfile(String name)
    {
        Profile p = new Profile(name);
        profileList.add(p);
    }

    public Profile getProfile(String profile)
    {
        for (Profile p : profileList)
        {
            if (p.getName().equals(profile))
            {
                return p;
            }
        }
        return null;
    }

    public void readProfiles()
    {
        profileList = profileFilesManager.readProfiles();
        if (!activeProfile.isEmpty())
        {
            for (Profile p : profileList)
            {
                if (p.getName().equals(activeProfile))
                {
                    return;
                }
            }
        }
        activeProfile = "";
    }

    public boolean removeProfile(String name)
    {
        Profile p = getProfile(name);
        if (p != null)
        {
            profileList.remove(p);
            activeProfile = "";
            saveAll(profileList);
        }
        return true;
    }

    public boolean saveAll(List<Profile> profileList)
    {
        this.profileList = profileList;
        return profileFilesManager.saveAll(this.profileList);
    }

    public List<Profile> getCopyOfProfileList()
    {
        readProfiles();
        List<Profile> list = new ArrayList<Profile>();
        for (Profile p : profileList)
        {
            list.add((Profile) streamer.fromXML(streamer.toXML(p)));
        }
        return list;
    }

    public int getHashCode(Profile p)
    {
        return streamer.toXML(p).hashCode();

    }

    public ProfileFilesManager getProfileFilesManager()
    {
        return profileFilesManager;
    }

    public void setProfileFilesManager(ProfileFilesManager profileFilesManager)
    {
        this.profileFilesManager = profileFilesManager;
    }

}
