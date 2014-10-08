/* Copyright 2009 EB2 International Limited */
package com.sabre.sabresonic.mockserver.core.sessionsinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sabre.sabresonic.mockserver.core.service.beans.PassengerName;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SessionsInfoManager
{
    private static final Logger LOG = LoggerFactory.getLogger(SessionsInfoManager.class);
    private static final SessionInfo NULL_SESSION_INFO = new SessionInfo() {
        @Override
        public void setPassengerName(PassengerName passengerName) { }
        @Override
        public void setOriginDestinations(String[] originDestinations) { }
    };
    /**
     * Map counting requests with the same name
     */
    private Map<String, SessionInfo> sessionCounterMap = new HashMap<String, SessionInfo>();
    private HashMap<String, Long> sessionLastUseMap = new HashMap<String, Long>();
    private int deleteOlderThan;

    public synchronized int getCount(String session, String request)
    {
        setCurrentTime(session);
        if (!sessionCounterMap.containsKey(session))
        {
            sessionCounterMap.put(session, new SessionInfo());
        }
        return sessionCounterMap.get(session).getNumber(request);
    }

    private synchronized void setCurrentTime(String session)
    {

        sessionLastUseMap.put(session, System.currentTimeMillis());
    }

    public synchronized void cleanUpMap()
    {
        int count = 0;
        long currentTime = System.currentTimeMillis();

        Iterator<Entry<String, Long>> it = sessionLastUseMap.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, Long> pairs = it.next();
            if (currentTime - pairs.getValue() > deleteOlderThan)
            {
                it.remove();
                sessionCounterMap.remove(pairs.getKey());
                count++;
            }
        }
        LOG.info("MapCleanUpTimer: Deleted " + count + " record(s). Left " + sessionCounterMap.size() + " record(s).");
    }

    public synchronized void cleanUpSessions()
    {
        int count = 0;
        Iterator<Entry<String, SessionInfo>> it = sessionCounterMap.entrySet().iterator();
        while (it.hasNext())
        {
                Entry<String, SessionInfo> pairs = it.next();
                it.remove();
                sessionCounterMap.remove(pairs.getKey());
                count++;
        }
        LOG.info("SessionCleanUpJMX: Deleted " + count + " record(s). Left " + sessionCounterMap.size() + " record(s).");
    }

    public int getDeleteOlderThan()
    {
        return deleteOlderThan;
    }

    public void setDeleteOlderThan(int deleteOlderThan)
    {
        this.deleteOlderThan = deleteOlderThan;
    }


    public synchronized String getSuffixForSession(String session)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter == null)
        {
            sessionCounterMap.put(session, new SessionInfo());
        }
        return sessionCounterMap.get(session).getSuffix();
    }

    public synchronized void setSuffixForSession(String session, String suffix)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            sessionCounter.setSuffix(suffix);
        }
    }

    public synchronized String getCreateTestTime(String session)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            return sessionCounter.getCreateDate();
        }
        return "";
    }

    public synchronized void setCreateTestTime(String session, String date)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            sessionCounter.setCreateDate(date);
        }
    }
    
    public synchronized long getDiffTime(String session)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            return sessionCounter.getTimeDiff();
        }
        return 0;
    }
    
    public synchronized void setDiffTime(String session, long diff)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            sessionCounter.setTimeDiff(diff);
        }
    }

    public synchronized void setPath(String session, String path)
    { 
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            sessionCounter.setPath(path);
        }
    }

    public synchronized String getPath(String session)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            return sessionCounter.getPath();
        }
        return "";
    }
    
    public synchronized String getTestName(String session)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            File f = new File(getPath(session));
            String suffixedName = f.getName();
            String suffix = getSuffixForSession(session);
            if (suffix.isEmpty())
                return suffixedName;
            else
                return suffixedName.replace(suffix, "");            
        }
        return "";
    }
    
    public synchronized String getAirline(String session)
    {
        SessionInfo sessionCounter = sessionCounterMap.get(session);
        if (sessionCounter != null)
        {
            File f = new File(getPath(session));
            
            File air = f.getParentFile().getParentFile();                        
            return  air.getName().substring(air.getName().indexOf("_") + 1);
        }
        return "";
    }


    public void addPassengerName(String sessionId, PassengerName passengerName) {
        SessionInfo sessionInfo = sessionCounterMap.get(sessionId);
        if ( sessionInfo == null ) {
            LOG.warn("Tried to add passenger name, but no session info object!!");
            return;
        }
        sessionInfo.setPassengerName(passengerName);
    }

    public PassengerName getPassengerName(String sessionId) {
        return getSession(sessionId).getPassengerName();
    }



    private SessionInfo getSession(String sessionId) {
        if ( sessionId == null ) {
            LOG.warn("Session id is null. Returning dummy object");
            return NULL_SESSION_INFO;
        }
        SessionInfo sessionInfo = sessionCounterMap.get(sessionId);
        if ( sessionInfo == null ) {
            LOG.warn("No session object associated with " + sessionId );
            return NULL_SESSION_INFO;
        }
        return sessionInfo;
    }

    public String[] getOriginDestinations(String sessionId) {
        return getSession(sessionId).getOriginDestinations();
    }


    public void setDepartureDates(String sessionId, String[] originDestinations) {
        getSession(sessionId).setOriginDestinations(originDestinations);
    }



    public void setVICounter(String sessionId) {
        SessionInfo sessionCounter = sessionCounterMap.get(sessionId);
        if (sessionCounter == null)
        {
            sessionCounter = new SessionInfo();
            sessionCounterMap.put(sessionId, sessionCounter);

        }
    }

    public void setExchangeStarted(String sessionId, boolean exchangeStarted) {
        SessionInfo sessionCounter = sessionCounterMap.get(sessionId);
        if (sessionCounter == null)
        {
            sessionCounter = new SessionInfo();
            sessionCounterMap.put(sessionId, sessionCounter);

        }
            sessionCounter.setExchangeStarted(exchangeStarted);
    }


    public boolean getExchangeStarted(String sessionId) {
        SessionInfo sessionCounter = sessionCounterMap.get(sessionId);
        if (sessionCounter != null)
        {
            return sessionCounter.getExchangeStarted();
        }
        return false;
    }
}
