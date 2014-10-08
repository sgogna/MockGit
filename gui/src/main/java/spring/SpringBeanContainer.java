/* Copyright 2009 EB2 International Limited */
package spring;

import javax.servlet.ServletContext;

import main.MainApplication;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import pages.components.customdiff.CustomDiffQuickAddWindow;
import pages.components.customdiff.CustomDiffWindow;
import util.SessionConfiguration;

import com.sabre.ssw.proxy.concurrent.fileManager.ConcurrentRequestsManager;
import com.sabre.ssw.proxy.concurrent.fileManager.ConcurrentRequestsRuleFilesManager;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

import fileManagers.MainFileManager;
import filehandlers.FilesTagsManager;
import filehandlers.ProfileManager;

public class SpringBeanContainer
{

    private static ServletContext servletContext;
    private static ApplicationContext applicationContext;

    static
    {
        servletContext = ((WebApplicationContext) MainApplication.getApplication().getContext()).getHttpSession().getServletContext();
        applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    private SpringBeanContainer()
    {
    }

    public static ProfileManager getProfileManagerBean()
    {
        return (ProfileManager) applicationContext.getBean("profileManager");
    }

    public static CustomDiffWindow getCustomDiffWindowBean()
    {
        return (CustomDiffWindow) applicationContext.getBean("customDiffWindow");
    }

    public static FilesTagsManager getFileTagsManager()
    {
        return (FilesTagsManager) applicationContext.getBean("fileTagsManager");
    }

    public static Configuration getConfiguration()
    {
        return (Configuration) applicationContext.getBean("testGuiConfig");
    }

    public static MainFileManager getMainFileManager()
    {
        return (MainFileManager) applicationContext.getBean("mainFileManager");
    }

    public static CustomDiffQuickAddWindow getCustomDiffQuickAddWindow()
    {
        return (CustomDiffQuickAddWindow) applicationContext.getBean("customDiffQuickAddWindow");
    }

    public static SessionConfiguration getSessionConfiguration()
    {
        return (SessionConfiguration) applicationContext.getBean("sessionConfiguration");
    }

    public static ConcurrentRequestsRuleFilesManager getConcurrentRequestsFilesManager()
    {
        return (ConcurrentRequestsRuleFilesManager) applicationContext.getBean("concurrentRequestsFilesManager");
    }
    
    public static ConcurrentRequestsManager getConcurrentRequestsManager()
    {
        return (ConcurrentRequestsManager) applicationContext.getBean("concurrentRequestsManager");
    }

}
