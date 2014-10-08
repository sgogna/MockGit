package pages;

import helpers.TableDirectory;
import helpers.TableRow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import listeners.EqualSwitchListener;
import main.MainApplication;
import pages.components.EqualSwitchCheckBox;
import pages.components.Icons;
import pages.components.NavigationPanel;
import pages.components.PagesMenuPanel;
import pages.components.customdiff.CustomDiffWindow;
import pages.components.customdiff.DiffTypeComboBox;
import proxy.compare.CompareResult;
import proxy.compare.FolderComparator;
import spring.SpringBeanContainer;

import com.sabre.ssw.proxy.compare.Comparator;
import com.sabre.ssw.proxy.compare.profile.Profile;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Table;

import constants.LabelConstants;
import fileManagers.MainFileManager;
import filehandlers.ProfileManager;
import filters.EqualSwitcherFilter;
import formatters.CustomDiff;

public class ComparisonTestTablePage extends AbstractPage implements Icons
{
    private static final long serialVersionUID = 1L;
    public static final String NAME_TESTS = "compareTests";

    private static final String FIRST_FILE_ID = "firstFile";
    private static final String SECOND_FILE_ID = "secondFile";
    private static final String FIRST_STATUS_ID = "firstStatus";
    private static final String SECOND_STATUS_ID = "secondStatus";
    private static final String COMPARISON_ID = "comparison";
    private static final String ERROR_STATUS_LABEL = "ERROR STATUS";

    private FolderComparator folderComparator = new FolderComparator();
    private PagesMenuPanel backToMainPanel;
    private Table table = new Table();

    private String styleName = "files";
    private CustomDiff customDiff = new CustomDiff();
    private Comparator comparator = new Comparator();

    private Button showCustomDiffWindowButton;
    private EqualSwitchCheckBox equalSwitchCheckBox;
    final CustomDiffWindow customDiffWindow;

    private DiffTypeComboBox diffTypeComboBox = new DiffTypeComboBox();

    private ProfileManager profileManager;
    private MainFileManager mainFileManager;
    private EqualSwitcherFilter filter = new EqualSwitcherFilter(COMPARISON_ID, LabelConstants.EQUAL_MARK);

    private NavigationPanel navigationPanel = null;

    public File currentPath1 = null;
    public File currentPath2 = null;
    private int profileHashCode = 0;

    public ComparisonTestTablePage()
    {
        profileManager = SpringBeanContainer.getProfileManagerBean();
        mainFileManager = SpringBeanContainer.getMainFileManager();
        customDiffWindow = SpringBeanContainer.getCustomDiffWindowBean();
        setName(NAME_TESTS);
        initialize();
    }

    private void initialize()
    {
        GridLayout grid = new GridLayout(1, 2);
        grid.setHeight("100%");
        grid.setWidth("100%");
        grid.setMargin(true);
        grid.setSpacing(true);
        backToMainPanel = new PagesMenuPanel(this);
        grid.addComponent(backToMainPanel, 0, 0);
        initializeShowCustomDiffWindowButton();
        initializeEqualSwitchCheckBox();
        table.setSizeFull();
        table.addContainerProperty(FIRST_STATUS_ID, String.class, null);
        table.addContainerProperty(FIRST_FILE_ID, File.class, null);
        table.setColumnExpandRatio(FIRST_FILE_ID, 1);
        table.addContainerProperty(COMPARISON_ID, String.class, null);
        table.addContainerProperty(SECOND_FILE_ID, File.class, null);
        table.setColumnExpandRatio(SECOND_FILE_ID, 1);
        table.addContainerProperty(SECOND_STATUS_ID, String.class, null);
        table.setSelectable(true);
        table.setColumnHeader(FIRST_STATUS_ID, ERROR_STATUS_LABEL);
        table.setColumnHeader(SECOND_STATUS_ID, ERROR_STATUS_LABEL);
        table.addListener(new ItemClickEvent.ItemClickListener()
        {
            private static final long serialVersionUID = 1L;

            public void itemClick(ItemClickEvent event)
            {
                if (event.isDoubleClick())
                {
                    
                        Item item = table.getItem(event.getItemId());
                        currentPath1 = (File) item.getItemProperty(FIRST_FILE_ID).getValue();
                        currentPath2 = (File) item.getItemProperty(SECOND_FILE_ID).getValue();
                        ComparisonFileTablePage comparisonTablePage = (ComparisonFileTablePage) ((MainApplication) getApplication()).getWindowWithoutRefresh(ComparisonFileTablePage.NAME_FILES);
                        comparisonTablePage.setBackupList(prepareList());
                        comparisonTablePage.createNewTable(currentPath1, currentPath2);
                        comparisonTablePage.addNavigationPanel(event.getItemId(), currentPath1, currentPath2);
                        open(new ExternalResource(comparisonTablePage.getURL()));
                    
                }
            }
        });
        table.setCellStyleGenerator(new Table.CellStyleGenerator()
        {
            private static final long serialVersionUID = 1L;

            public String getStyle(Object itemId, Object propertyId)
            {
                String suffix = "";
                if (propertyId == FIRST_STATUS_ID || propertyId == SECOND_STATUS_ID)
                {
                    return "error" + suffix;
                }
                if (propertyId == COMPARISON_ID)
                    return "middle" + suffix;

                String tmp = table.getItem(itemId)
                        .getItemProperty(FIRST_FILE_ID).toString();

                if (tmp.equals(LabelConstants.NO_FILE))
                {
                    return "gray" + suffix;
                }

                tmp = table.getItem(itemId).getItemProperty(SECOND_FILE_ID)
                        .toString();

                if (tmp.equals(LabelConstants.NO_FILE))
                {
                    return "gray" + suffix;
                }
                return "black" + suffix;

            }
        });
        table.addStyleName(styleName);

        grid.addComponent(table, 0, 1);
        grid.setRowExpandRatio(1, 1.0f);

        prepareDiffTypeComboBox();
        this.setContent(grid);
    }

    public void disableNavPanel()
    {
        if (navigationPanel != null)
            navigationPanel.setVisible(false);
    }


    @Override
    public void refresh()
    {
        equalSwitchCheckBox.setValue(profileManager.isEqualSwitch());
        refreshDiffTypeComboBox();

    }

    private void initializeShowCustomDiffWindowButton()
    {
        showCustomDiffWindowButton = new Button("Show custom diff editor");
        showCustomDiffWindowButton.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                showCustomDiffWindow();
            }
        });
    }

    private void initializeEqualSwitchCheckBox()
    {
        equalSwitchCheckBox = new EqualSwitchCheckBox(profileManager.isEqualSwitch());
        equalSwitchCheckBox.addListener(new ClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                refreshTableFilter();
            }
        });
        equalSwitchCheckBox.addListener(new EqualSwitchListener());
    }

    private void showCustomDiffWindow()
    {
        try
        {
            customDiffWindow.show(currentPath1, this);
        }
        catch (Exception e)
        {
        }
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
                    changeProfileRefreshTable();
                }
            }
        });
        diffTypeComboBox.setImmediate(true);
    }

    private void changeDiffProfile(String profile)
    {
        if (profile != null && !profile.isEmpty())
        {
            profileManager.setActiveProfile(profile);
        }
        else
        {
            profileManager.setActiveProfile("Default");
        }
    }

    private boolean compareTests(File dir1, File dir2)
    {
        String activeProfile = profileManager.getActiveProfileName();
        Profile profile = profileManager.getActiveProfile();
        CompareResult dirCompareResult;
        if (profile == null)
        {
            dirCompareResult = folderComparator.compareDirs(dir1.getAbsolutePath(), dir2.getAbsolutePath());
        }
        else
        {
            dirCompareResult = folderComparator.compareDirs(dir1.getAbsolutePath(), dir2.getAbsolutePath(), profile.getIncludeList(), profile.getExcludeList());
        }

        int newFilesNumber = dirCompareResult.getNewFilesInFirstDir().size() + dirCompareResult.getNewFilesInSecondDir().size();
        if (newFilesNumber != 0)
        {
            return false;
        }
        if (activeProfile.equals(FolderComparator.NAME) || (profile != null && !profile.isContentCompare()))
        {
            return true;
        }
        List<File[]> testFiles = new ArrayList<File[]>();
        for (File file : dirCompareResult.getEqualFilesFromFirstDir())
        {
            testFiles.add(new File[] { file, MainFileManager.getEqualFileFromSecondDir(dir1.getAbsolutePath(), dir2.getAbsolutePath(), file) });
        }
        for (Iterator<File[]> iterator = testFiles.iterator(); iterator.hasNext();)
        {
            File[] filesPair = iterator.next();
            File firstFile = filesPair[0];
            File secondFile = filesPair[1];
            if (diffFiles(firstFile, secondFile))
            {
                return false;
            }
        }
        return true;
    }

    private void refreshTestsTable()
    {
        Filterable filterable = (Filterable) table.getContainerDataSource();
        filterable.removeContainerFilter(filter);
        for (Object id : table.getItemIds())
        {
            Item item = table.getItem(id);
            File firstTest = (File) item.getItemProperty(FIRST_FILE_ID).getValue();
            File secondTest = (File) item.getItemProperty(SECOND_FILE_ID).getValue();
            if (compareTests(firstTest, secondTest))
            {
                item.getItemProperty(COMPARISON_ID).setValue(LabelConstants.EQUAL_MARK);
            }
            else
            {
                item.getItemProperty(COMPARISON_ID).setValue(LabelConstants.DIFF_MARK);
            }
        }
        filterable.addContainerFilter(filter);
    }


    private void refreshTable()
    {
            refreshTestsTable();
    }

    private void refreshTableFilter()
    {
        Filterable filterable = (Filterable) table.getContainerDataSource();
        filterable.removeContainerFilter(filter);
        filterable.addContainerFilter(filter);
    }

    private void refreshDiffTypeComboBox()
    {
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
        if (profileManager.getActiveProfileName().isEmpty())
        {
            diffTypeComboBox.setValue("Default");
        }
        else
        {
            diffTypeComboBox.setValue(profileManager.getActiveProfileName());
        }
        diffTypeComboBox.setListenerEnabled(true);
        changeProfileRefreshTable();
    }

    @Override
    public void attach()
    {
        super.attach();
        initializeBackPanel();
    }

    private void initializeBackPanel()
    {
        backToMainPanel.addComponent(diffTypeComboBox);
        backToMainPanel.addComponent(showCustomDiffWindowButton);
        backToMainPanel.addComponent(equalSwitchCheckBox);
    }

    private List<TableRow> prepareList()
    {
        List<TableRow> toReturn = new ArrayList<TableRow>();

        File path1, path2;
        path1 = path2 = null;
        String tmp = null;
        Collection< ? > collection = table.getItemIds();
        for (Object ID : collection)
        {
            Item itemID = table.getItem(ID);
            if (itemID != null)
            {
                tmp = table.getItem(ID).getItemProperty(FIRST_FILE_ID).toString();

                if (!tmp.equals(LabelConstants.NO_FILE))
                    path1 = (File) itemID.getItemProperty(FIRST_FILE_ID).getValue();
                else
                    path1 = null;

                tmp = table.getItem(ID).getItemProperty(SECOND_FILE_ID).toString();

                if (!tmp.equals(LabelConstants.NO_FILE))
                    path2 = (File) itemID.getItemProperty(SECOND_FILE_ID).getValue();
                else
                    path2 = null;

                toReturn.add(new TableRow(Integer.parseInt("" + ID), path1, path2));
            }
        }
        return toReturn;
    }


    public void createNewTable(File root, String run)
    {
        List<File[]> testFolderPairs = new ArrayList<File[]>();
        for (File f : mainFileManager.getTestForRun(run))
        {
            File basicTest = new File(mainFileManager.getBasicTestFolderPath(f, null));
            if (basicTest != null)
            {
                File recordFolder = basicTest;
                File compareFolder = f;
                testFolderPairs.add(new File[] { recordFolder, compareFolder });
            }
        }
        createNewTable(testFolderPairs);
    }

    public void createNewTable(List<File[]> testFolderPairs)
    {
        if (testFolderPairs.size() > 0)
        {
            currentPath1 = (testFolderPairs.get(0)[0]).getParentFile();
        }
        table.removeAllItems();
        // TODO
        // remove hardcoded names
        table.setColumnHeader(FIRST_FILE_ID, "Record");
        table.setColumnHeader(SECOND_FILE_ID, "Compare");
        for (int i = 0; i < testFolderPairs.size(); i++)
        {
            File recordFolder = testFolderPairs.get(i)[0];
            File compareFolder = testFolderPairs.get(i)[1];

            Profile profile = profileManager.getActiveProfile();
            CompareResult dirCompareResult;
            if (profile == null)
            {
                dirCompareResult = folderComparator.compareDirs(recordFolder.getAbsolutePath(), compareFolder.getAbsolutePath());
            }
            else
            {
                dirCompareResult = folderComparator.compareDirs(recordFolder.getAbsolutePath(), compareFolder.getAbsolutePath(), profile.getIncludeList(), profile.getExcludeList());
            }

            String recordErrorStatus = (dirCompareResult.getErrorFilesInFirstDir().size() > 0) ? LabelConstants.STATUS_ERROR : null;
            String compareErrorStatus = (dirCompareResult.getErrorFilesInSecondDir().size() > 0) ? LabelConstants.STATUS_ERROR : null;
            Object[] row = new Object[] { recordErrorStatus,
                    new TableDirectory(recordFolder.getAbsolutePath()), null,
                    new TableDirectory(compareFolder.getAbsolutePath()), compareErrorStatus };
            table.addItem(row, null);
        }
        refreshTestsTable();
    }

    private void changeProfileRefreshTable()
    {
        String profile = (String) diffTypeComboBox.getValue();
        changeDiffProfile(profile);
        if (profileManager.getActiveProfile() != null)
        {
            int code = profileManager.getHashCode(profileManager.getActiveProfile());
            if (code != profileHashCode)
            {
                refreshTable();
                profileHashCode = code;
            }
        }
        else
        {
            refreshTable();
            profileHashCode = 0;
        }
    }

    private boolean diffFiles(File file1, File file2)
    {
        boolean testNewComparator = false;
        boolean result = false;
        if (testNewComparator)
        {
            result = !comparator.isEqualsFiles(file1, file2, profileManager.getActiveProfile());
        }
        else
        {
            result = customDiff.isDiff(file1, file2);
        }
        return result;
    }
}
