package main;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pages.AbstractPage;
import pages.ComparisonFileTablePage;
import pages.ComparisonTestTablePage;
import pages.ConcurrentRequestEditPage;
import pages.HomePage;
import pages.ShowFilesPage;
import pages.TestStatusPage;
import pages.ViewFilePage;
import spring.Configuration;
import spring.SpringBeanContainer;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Window;

public class MainApplication extends Application
{

    private static final long serialVersionUID = 1631976257046830326L;
    private static final Logger LOG = LoggerFactory.getLogger(MainApplication.class);
    private static String customTagsFilePath;
    private static Application application;
    private ComparisonTestTablePage comparisonTestsTablePage;
    private ComparisonFileTablePage comparisonFilesTablePage;
    private ViewFilePage viewFilePage;
    private TestStatusPage testStatusPage;
    private ShowFilesPage showFilesPage;
    private ConcurrentRequestEditPage concurrentRequestEditPage;

    private String recDir = ".";
    private String page = "";

    public static Application getApplication()
    {
        return application;
    }

    @Override
    public void init()
    {
        application = this;
        initializeProperties();
        setMainWindow(new HomePage(recDir));

        comparisonTestsTablePage = new ComparisonTestTablePage();
        comparisonFilesTablePage = new ComparisonFileTablePage();
        testStatusPage = new TestStatusPage();
        viewFilePage = new ViewFilePage();
        showFilesPage = new ShowFilesPage();
        concurrentRequestEditPage = new ConcurrentRequestEditPage();

        addWindow(comparisonTestsTablePage);
        addWindow(comparisonFilesTablePage);
        addWindow(viewFilePage);
        addWindow(testStatusPage);
        addWindow(showFilesPage);
        addWindow(concurrentRequestEditPage);

    }

    @Override
    public Window getWindow(String name)
    {
        Window w = super.getWindow(name);

        if (w == null)
        {
            w = new HomePage(recDir);
            w.setName(name);
            addWindow(w);
            w.open(new ExternalResource(w.getURL()));
        }
        if (!page.equals(name))
        {
            ((AbstractPage) w).refresh();
        }
        page = name;
        return w;
    }

    public Window getWindowWithoutRefresh(String name)
    {
        return super.getWindow(name);
    }

    private void initializeProperties()
    {
        setTheme("guiTheme");
        Configuration configuration = SpringBeanContainer.getConfiguration();

        recDir = configuration.getRecDir();
        if (!(new File(recDir).isDirectory()))
            new File(recDir).mkdir();

        customTagsFilePath = configuration.getCustomTagsFilePath();
        if (!(new File(customTagsFilePath).isDirectory()))
            new File(customTagsFilePath).mkdir();
        LOG.info("Tag confirugation files will be uploaded to " + customTagsFilePath);

        if (System.getProperty("NewDiff") != null && (System.getProperty("NewDiff").equals("true") || System.getProperty("NewDiff").equals("false")))
        {
            SpringBeanContainer.getSessionConfiguration().NEW_DIFF = Boolean.parseBoolean(System.getProperty("NewDiff"));
        }
    }

    public static final String getCustomTagsFilePath()
    {
        return customTagsFilePath;
    }

    public static final void setCustomTagsFilePath(String customTagsFileName)
    {
        MainApplication.customTagsFilePath = customTagsFileName;
    }

}