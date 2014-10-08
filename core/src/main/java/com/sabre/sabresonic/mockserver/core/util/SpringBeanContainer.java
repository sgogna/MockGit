/* Copyright 2009 EB2 International Limited */
package com.sabre.sabresonic.mockserver.core.util;

import com.sabre.sabresonic.mockserver.core.message.datagrabbers.DataGrabber;
import com.sabre.sabresonic.mockserver.core.message.replacers.DataReplacer;
import com.sabre.sabresonic.mockserver.core.message.replacers.DateReplaceEngine;
import com.sabre.sabresonic.mockserver.core.sessionsinfo.SessionsInfoManager;
import org.springframework.context.ApplicationContext;

public class SpringBeanContainer
{
    private static SessionsInfoManager sessionsInfoManager;
    private static DataReplacer dataReplacer;
    private static DataGrabber dataGrabber;
    private static DateReplaceEngine dateReplaceEngine;

    private SpringBeanContainer()
    {
    }

   public static void setApplicationContext(ApplicationContext applicationContext)
    {
        dataGrabber = (DataGrabber) applicationContext.getBean("dataGrabber");
        dataReplacer = (DataReplacer) applicationContext.getBean("dataReplacer");
        sessionsInfoManager =  (SessionsInfoManager) applicationContext.getBean("sessionsInfoManager");
        dateReplaceEngine =  (DateReplaceEngine) applicationContext.getBean("dateReplaceEngine");

    }

    public static SessionsInfoManager getSessionsInfoManager() {
        return sessionsInfoManager;
    }

    public static DataReplacer getDataReplacer() {
        return dataReplacer;
    }

    public static DataGrabber getDataGrabber() {
        return dataGrabber;
    }

    public static DateReplaceEngine getDateReplaceEngine() {
        return dateReplaceEngine;
    }
}
