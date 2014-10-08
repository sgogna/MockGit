package pages.components;

import fileManagers.MainFileManager;
import filehandlers.ProfileManager;
import helpers.TreeHandler;

import java.io.File;
import java.util.Iterator;

import main.MainApplication;
import pages.ConcurrentRequestEditPage;
import pages.HomePage;
import pages.components.customdiff.DiffTypeComboBox;
import proxy.compare.FolderComparator;
import spring.SpringBeanContainer;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;

public class MenuPanel extends Panel
{

    private static final long serialVersionUID = 1L;
    private HomePage mainPage;
    private TreeHandler treeHandler;
    private ProfileManager profileManager;
    private DiffTypeComboBox diffTypeComboBox = new DiffTypeComboBox();
    private DiffTypeComboBox runComboBox = new DiffTypeComboBox();
    private MainFileManager mainFileManager;
    private HorizontalLayout horizontalLayoutForButtons = null;

    public MenuPanel(final String recDir, Tree tree, HomePage page)
    {
        profileManager = SpringBeanContainer.getProfileManagerBean();
        mainFileManager = SpringBeanContainer.getMainFileManager();

        this.mainPage = page;
        this.treeHandler = new TreeHandler(tree);

        runComboBox.setNewItemsAllowed(false);
        runComboBox.setImmediate(true);
        runComboBox.setNullSelectionAllowed(false);
        runComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        runComboBox.setInputPrompt("Select a run to compare");
        runComboBox.setWidth(300, UNITS_PIXELS);
        runComboBox.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                if (runComboBox.isListenerEnabled())
                {
                    mainPage.switchToCompareRunsPage(recDir,
                            (String) runComboBox.getValue());
                }
            }
        });

        horizontalLayoutForButtons = new HorizontalLayout();

        Button compareAll = new Button("Compare All");
        compareAll.addListener(new ClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                treeHandler.compareAll(recDir);

            }
        });

        Button concurrentButton = new Button("Concurrent request rules");
        concurrentButton.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                ConcurrentRequestEditPage concurrentRequestEditPage = (ConcurrentRequestEditPage) ((MainApplication) getApplication()).getWindowWithoutRefresh(ConcurrentRequestEditPage.NAME);
                getWindow().open(new ExternalResource(concurrentRequestEditPage.getURL()));
            }
        });

        horizontalLayoutForButtons.addComponent(runComboBox);
        horizontalLayoutForButtons.addComponent(diffTypeComboBox);
        horizontalLayoutForButtons.addComponent(concurrentButton);
        horizontalLayoutForButtons.setExpandRatio(concurrentButton, 1.0f);
        horizontalLayoutForButtons.setComponentAlignment(concurrentButton, Alignment.MIDDLE_RIGHT);
        horizontalLayoutForButtons.setMargin(true);
        horizontalLayoutForButtons.setSpacing(true);
        horizontalLayoutForButtons.setSizeFull();

        setContent(horizontalLayoutForButtons);

        prepareDiffTypeComboBox();
    }

    public void compareDirsAction()
    {
        Iterator<File> c = treeHandler.compareDirs();
        if (c == null)
            mainPage.showNotification("Choose exacly 2 folders");
        else
            mainPage.switchToComparisonWindow(c);
    }

    public void refresh()
    {
        runComboBox.setListenerEnabled(false);
        runComboBox.removeAllItems();
        for (String s : mainFileManager.getRuns())
        {
            if (!s.equals("*"))
            {
                runComboBox.addItem(s);
            }
        }
        refreshDiffTypeComboBox();
        runComboBox.setListenerEnabled(true);
    }

    private void prepareDiffTypeComboBox()
    {
        diffTypeComboBox.setNullSelectionAllowed(false);
        diffTypeComboBox.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                if (diffTypeComboBox.isListenerEnabled())
                {
                    MenuPanel.this.changeDiffProfile();
                }
            }
        });
        diffTypeComboBox.setImmediate(true);
        refreshDiffTypeComboBox();
    }

    protected void changeDiffProfile()
    {
        String profile = (String) diffTypeComboBox.getValue();
        if (profile != null && !profile.isEmpty())
        {

            profileManager.setActiveProfile(profile);
        }
        else
        {
            profileManager.setActiveProfile("Default");
        }
    }

    public void refreshDiffTypeComboBox()
    {

        String profile = (String) diffTypeComboBox.getValue();
        diffTypeComboBox.setListenerEnabled(false);
        String activeProfile = profileManager.getActiveProfileName();
        diffTypeComboBox.removeAllItems();
        // TODO
        // remove hardcoded items
        diffTypeComboBox.addItem("Default");
        diffTypeComboBox.addItem(FolderComparator.NAME);
        for (Profile p : profileManager.getProfileList())
        {
            diffTypeComboBox.addItem(p.getName());
        }
        if (activeProfile.equals(FolderComparator.NAME))
        {
            profileManager.setActiveProfile(FolderComparator.NAME);
        }
        if ((profile != null && !profile.equals(profileManager.getActiveProfileName())))
        {
            diffTypeComboBox.setListenerEnabled(true);
        }
        if (profileManager.getActiveProfileName().isEmpty())
        {
            diffTypeComboBox.select("Default");
        }
        else
        {
            diffTypeComboBox.select(profileManager.getActiveProfileName());
        }

        diffTypeComboBox.setListenerEnabled(true);
    }
}
