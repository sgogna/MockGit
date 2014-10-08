package pages;

import helpers.TableFile;
import helpers.TableRow;
import helpers.TableRowList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Table;

import constants.LabelConstants;
import fileManagers.InfoFileManager;
import fileManagers.MainFileManager;
import filehandlers.ProfileManager;
import filters.EqualSwitcherFilter;
import formatters.CustomDiff;

public class ComparisonFileTablePage extends AbstractPage implements Icons
{
    private static final long serialVersionUID = 1L;

    public static final String NAME_FILES = "compareFiles";

    private static final String FIRST_FILE_ID = "firstFile";
    private static final String SECOND_FILE_ID = "secondFile";
    private static final String FIRST_STATUS_ID = "firstStatus";
    private static final String SECOND_STATUS_ID = "secondStatus";
    private static final String COMPARISON_ID = "comparison";
    private static final String FIRST_TEST_ORDER = "firstOrder";
    private static final String SECOND_TEST_ORDER = "secondOrder";
    private static final String ERROR_STATUS_LABEL = "ERROR STATUS";
    private static final String ORDER_LABEL = "ORDER";

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
    private EqualSwitcherFilter filter = new EqualSwitcherFilter(COMPARISON_ID, LabelConstants.EQUAL_MARK);

    private NavigationPanel navigationPanel = null;
    private List<TableRow> backupList = new ArrayList<TableRow>();

    public File currentPath1 = null;
    public File currentPath2 = null;
    private int profileHashCode = 0;

    public ComparisonFileTablePage()
    {
        profileManager = SpringBeanContainer.getProfileManagerBean();
        customDiffWindow = SpringBeanContainer.getCustomDiffWindowBean();
        setName(NAME_FILES);
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
        table.addContainerProperty(FIRST_TEST_ORDER, Integer.class, null);
        table.addContainerProperty(FIRST_FILE_ID, File.class, null);
        table.setColumnExpandRatio(FIRST_FILE_ID, 1);
        table.addContainerProperty(COMPARISON_ID, String.class, null);
        table.addContainerProperty(SECOND_FILE_ID, File.class, null);
        table.setColumnExpandRatio(SECOND_FILE_ID, 1);
        table.addContainerProperty(SECOND_TEST_ORDER, Integer.class, null);
        table.addContainerProperty(SECOND_STATUS_ID, String.class, null);
        table.setSelectable(true);
        table.setColumnHeader(FIRST_STATUS_ID, ERROR_STATUS_LABEL);
        table.setColumnHeader(SECOND_STATUS_ID, ERROR_STATUS_LABEL);
        table.setColumnHeader(FIRST_TEST_ORDER, ORDER_LABEL);
        table.setColumnHeader(SECOND_TEST_ORDER, ORDER_LABEL);
        table.addListener(new ItemClickEvent.ItemClickListener()
        {
            private static final long serialVersionUID = 1L;

            public void itemClick(ItemClickEvent event)
            {
                if (event.isDoubleClick())
                {
                    initializeShowFilesPage(event.getItemId());
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

    public void setBackupList(List<TableRow> backupList)
    {
        this.backupList = backupList;
    }

    public void disableNavPanel()
    {
        if (navigationPanel != null)
            navigationPanel.setVisible(false);
    }

    public void addNavigationPanel(Object itemID, File file1, File file2)
    {
        if (navigationPanel == null)
        {
            navigationPanel = new NavigationPanel();
            backToMainPanel.addComponent(navigationPanel, new Alignment(Bits.ALIGNMENT_RIGHT));
        }
        navigationPanel.setVisible(true);
        TableRow currentRow = new TableRow(Integer.parseInt("" + itemID), file1, file2);
        TableRowList rowList = new TableRowList(backupList, currentRow);
        navigationPanel.setTableInfo(rowList, this);
    }

    @Override
    public void refreshAfterClick()
    {
        TableRowList rowList = navigationPanel.getRowList();
        TableRow currentRow = rowList.getCurrent();
        navigationPanel.setTableInfo(rowList, this);
        createNewTable(currentRow.getFirstFile(), currentRow.getSecondFile());
        refreshTable();
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

    private boolean isFilesEquals(File file1, File file2)
    {
        boolean equal;
        String activeProfile = profileManager.getActiveProfileName();
        if (activeProfile.equals(FolderComparator.NAME))
        {
            equal = (!file1.getName().equals(LabelConstants.NO_FILE) && !file2.getName().equals(LabelConstants.NO_FILE));
        }
        else
        {
            equal = !isDifferent(file1, file2);
        }
        return equal;
    }

    private void refreshFilesTable()
    {
        Filterable filterable = (Filterable) table.getContainerDataSource();
        filterable.removeContainerFilter(filter);
        for (Object id : table.getItemIds())
        {
            Item tableRow = table.getItem(id);
            if (tableRow == null)
            {
                continue;
            }
            File file1 = (File) tableRow.getItemProperty(FIRST_FILE_ID).getValue();
            File file2 = (File) tableRow.getItemProperty(SECOND_FILE_ID).getValue();
            if (!file1.getName().equals(LabelConstants.NO_FILE) && !file2.getName().equals(LabelConstants.NO_FILE))
            {
                boolean compareResult = isFilesEquals(file1, file2);
                if (compareResult)
                {
                    tableRow.getItemProperty(COMPARISON_ID).setValue(LabelConstants.EQUAL_MARK);
                }
                else
                {
                    tableRow.getItemProperty(COMPARISON_ID).setValue(LabelConstants.DIFF_MARK);
                }
            }
            else
            {
                tableRow.getItemProperty(COMPARISON_ID).setValue(LabelConstants.DIFF_MARK);
            }
        }
        filterable.addContainerFilter(filter);
    }

    private void refreshTable()
    {
        createNewTable(currentPath1, currentPath2);
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

    private void initializeShowFilesPage(Object selection)
    {
        Item item = table.getItem(selection);
        File filepath1 = null;
        File filepath2 = null;
        String tmp = table.getItem(selection).getItemProperty(FIRST_FILE_ID)
                .toString();

        if (!tmp.equals(LabelConstants.NO_FILE))
            filepath1 = (File) item.getItemProperty(FIRST_FILE_ID).getValue();
        tmp = table.getItem(selection).getItemProperty(SECOND_FILE_ID).toString();
        if (!tmp.equals(LabelConstants.NO_FILE))
            filepath2 = (File) item.getItemProperty(SECOND_FILE_ID).getValue();

        TableRow selectedRow = new TableRow(Integer.parseInt("" + selection), filepath1, filepath2);
        List<TableRow> rowList = prepareList();
        TableRowList list = new TableRowList(rowList, selectedRow);
        ShowFilesPage showFilesPage = (ShowFilesPage) ((MainApplication) getApplication()).getWindowWithoutRefresh(ShowFilesPage.NAME);
        showFilesPage.showFiles(list);
        open(new ExternalResource(showFilesPage.getURL()));
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

    public void createNewTable(File file1, File file2)
    {
        currentPath1 = file1;
        currentPath2 = file2;
        if (file1 == null || file2 == null)
        {
            return;
        }
        Object sortPropertyId = table.getSortContainerPropertyId();
        boolean sortAscending = table.isSortAscending();
        table.removeAllItems();
        table.setColumnHeader(FIRST_FILE_ID, titleToShow(file1));
        table.setColumnHeader(SECOND_FILE_ID, titleToShow(file2));
        addFolderToTable(file1.getAbsolutePath(), file2.getAbsolutePath());
        refreshFilesTable();
        table.sort(new Object[] { sortPropertyId, FIRST_FILE_ID}, new boolean[] { sortAscending, true});
    }

    private void addFolderToTable(String path1, String path2)
    {
        Profile profile = profileManager.getActiveProfile();
        CompareResult dirCompareResult;
        if (profile == null)
        {
            dirCompareResult = folderComparator.compareDirs(path1, path2);
        }
        else
        {
            dirCompareResult = folderComparator.compareDirs(path1, path2, profile.getIncludeList(), profile.getExcludeList());
        }
        if (dirCompareResult == null)
        {
            this.showNotification("Incorrect path for test dir!");
            return;
        }
        List<String> errorFiles1 = getStringFileList(dirCompareResult.getErrorFilesInFirstDir());
        List<String> errorFiles2 = getStringFileList(dirCompareResult.getErrorFilesInSecondDir());
        List<String> firstOrderList = InfoFileManager.getOrderInfoFile(path1).getOrderList();
        List<String> secondOrderList = InfoFileManager.getOrderInfoFile(path2).getOrderList();
        for (File file : dirCompareResult.getEqualFilesFromFirstDir())
        {
            addItemToTable(file, MainFileManager.getEqualFileFromSecondDir(path1,path2, file), errorFiles1, errorFiles2, firstOrderList, secondOrderList);
        }
        for (File file : dirCompareResult.getNewFilesInFirstDir())
        {
            addItemToTable(file, new File(LabelConstants.NO_FILE), errorFiles1, errorFiles2, firstOrderList, secondOrderList);
        }
        for (File file : dirCompareResult.getNewFilesInSecondDir())
        {
            addItemToTable(new File(LabelConstants.NO_FILE), file, errorFiles1, errorFiles2, firstOrderList, secondOrderList);
        }
    }

    private List<String> getStringFileList(List<File> fileList)
    {
        List<String> stringList = new ArrayList<String>();
        for (File i : fileList)
            stringList.add(i.getName());
        return stringList;
    }

    private void addItemToTable(File firstFile, File secondFile, List<String> errorFiles1, List<String> errorFiles2, List<String> firstOrderList, List<String> secondOrderList)
    {
        if (isCompareDirs() && (firstFile.getName().matches(".*\\.zhd") || secondFile.getName().matches(".*\\.zhd")))
            return;

        int firstIndex = firstOrderList.indexOf(firstFile.getName().substring(0, firstFile.getName().lastIndexOf(".") < 0 ? firstFile.getName().length() : firstFile.getName().lastIndexOf(".") - 3));
        int secondIndex = secondOrderList.indexOf(secondFile.getName().substring(0, secondFile.getName().lastIndexOf(".") < 0 ? secondFile.getName().length() : secondFile.getName().lastIndexOf(".") - 3));
        Object[] tableRow = new Object[] { null, firstIndex < 0 ? null : firstIndex + 1, new TableFile(firstFile.getAbsolutePath()),
                null, new TableFile(secondFile.getAbsolutePath()), secondIndex < 0 ? null : secondIndex + 1, null };
        if (errorFiles1.contains(firstFile.getName()))
        {
            tableRow[0] = LabelConstants.STATUS_ERROR;
        }
        if (errorFiles2.contains(secondFile.getName()))
        {
            tableRow[6] = LabelConstants.STATUS_ERROR;
        }
        table.addItem(tableRow, null);

    }

    private boolean isCompareDirs()
    {
        return profileManager.getActiveProfileName().equals("Compare dirs");
    }

    private String titleToShow(File filename)
    {
        if (filename == null)
            return "";
        return filename.getParentFile().getName();

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

    private boolean isDifferent(File file1, File file2)
    {
        boolean result = false;
        if (SpringBeanContainer.getSessionConfiguration().NEW_DIFF)
        {
            boolean result1 = !comparator.isEqualsFiles(file1, file2, profileManager.getActiveProfile());
            result = result1;
        }else {
            result = customDiff.isDiff(file1, file2);
            
        }

        return result;
    }
}
