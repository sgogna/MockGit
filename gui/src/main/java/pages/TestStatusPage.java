/* Copyright 2009 EB2 International Limited */
package pages;

import helpers.FileReader;
import helpers.TableFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pages.components.PagesMenuPanel;
import pages.subwindows.ChooseDestinationDirForCopyWindow;
import pages.subwindows.ConfirmationWindow;
import proxy.compare.CopyManager;
import spring.SpringBeanContainer;

import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import constants.LabelConstants;
import fileManagers.InfoFileManager;
import fileManagers.MainFileManager;
import filehandlers.ProfileManager;

public class TestStatusPage extends AbstractPage
{
    private static final long serialVersionUID = 1L;
    private static final String TEST_LABEL = "TEST NAME";
    private static final String TEST_RESULT_LABEL = "TEST RESULT";
    private static final String COMPARE_RESULT_LABEL = "COMPARE STATUS";
    private static final String ERROR_STATUS_LABEL = "ERROR STATUS";
    private static final String COMPARE_ALGORITHM_LABEL = "COMPARE ALGORITHM";
    private static final String COMPARE_TO_RECORD = "Compare to record";
    private static final String REPLACE_RECORD = "Replace record(s)";
    private static final String REPLACE_RECORD_WITH_DIR_SELECTION = "Replace record(s) in...";
    protected static final String NAME = "teststatus";

    private File run;
    private Table table;
    private Label runLabel;
    private PagesMenuPanel backToMainPanel;
    private VerticalLayout verticalLayout;
    private Action replaceRecordAction;
    private Action replaceRecordWithDirSelectionAction;
    private Action compareToRecordAction;
    private ProfileManager profileManager;
    private MainFileManager mainFileManager;
    private String styleName = "files";

    private ConfirmationWindow confirmationWindow;
    private ChooseDestinationDirForCopyWindow chooseDestinationDirForCopyWindow;

    public TestStatusPage()
    {
        setName(NAME);
        chooseDestinationDirForCopyWindow = new ChooseDestinationDirForCopyWindow();
        chooseDestinationDirForCopyWindow.addOkButtonListener(createChoosDestinationOkButtonListener());
        chooseDestinationDirForCopyWindow.setOkCloseEnabled(false);
        profileManager = SpringBeanContainer.getProfileManagerBean();
        mainFileManager = SpringBeanContainer.getMainFileManager();
        backToMainPanel = new PagesMenuPanel(this);
        runLabel = new Label();
        runLabel.setContentMode(Label.CONTENT_XHTML);
        verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(backToMainPanel);
        verticalLayout.addComponent(runLabel);
        prepareTable();
        verticalLayout.addComponent(table);
        verticalLayout.setExpandRatio(table, 1.0f);
        setContent(verticalLayout);
    }

    private ClickListener createChoosDestinationOkButtonListener()
    {
        return new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                String s = chooseDestinationDirForCopyWindow.getSelectedFolder();
                if (s != null)
                {
                    copyFiles(s);
                    chooseDestinationDirForCopyWindow.close();
                }
                else
                {
                    TestStatusPage.this.showNotification("Select destination!", Notification.TYPE_HUMANIZED_MESSAGE);
                }
            }
        };
    }

    private void prepareTable()
    {
        table = new Table();
        table.setSizeFull();
        table.addContainerProperty(TEST_LABEL, File.class, null);
        table.setColumnExpandRatio(TEST_LABEL, 1);
        table.addContainerProperty(TEST_RESULT_LABEL, String.class, null);
        table.addContainerProperty(COMPARE_RESULT_LABEL, String.class, null);
        table.addContainerProperty(COMPARE_ALGORITHM_LABEL, String.class, null);
        table.addContainerProperty(ERROR_STATUS_LABEL, String.class, null);
        table.setMultiSelect(true);
        table.setCellStyleGenerator(new Table.CellStyleGenerator()
        {
            private static final long serialVersionUID = 1L;

            public String getStyle(Object itemId, Object propertyId)
            {
                if (TEST_RESULT_LABEL.equals(propertyId))
                {
                    String tmp = table.getItem(itemId).getItemProperty(TEST_RESULT_LABEL).toString();
                    if (tmp.equals(LabelConstants.TEST_FAILED))
                    {
                        return "error";
                    }
                    return "passed";
                }
                else if (COMPARE_RESULT_LABEL.equals(propertyId))
                {
                    Boolean result = new Boolean((String) table.getItem(itemId).getItemProperty(propertyId).getValue());
                    if (result)
                    {
                        return "passed";
                    }
                    return "error";
                }
                else if (ERROR_STATUS_LABEL.equals(propertyId))
                {
                    String errorStatus = (String) table.getItem(itemId).getItemProperty(propertyId).getValue();
                    if (errorStatus.equals(LabelConstants.STATUS_OK))
                    {
                        return "passed";
                    }
                    return "error";
                }
                else if (COMPARE_ALGORITHM_LABEL.equals(propertyId))
                {
                    return "black-center";
                }
                return "black";
            }
        });
        table.setSelectable(true);
        table.setImmediate(true);
        table.addStyleName(styleName);
        replaceRecordAction = new Action(REPLACE_RECORD);
        replaceRecordWithDirSelectionAction = new Action(REPLACE_RECORD_WITH_DIR_SELECTION);
        compareToRecordAction = new Action(COMPARE_TO_RECORD);
        table.addActionHandler(new Action.Handler()
        {
            private static final long serialVersionUID = 1L;

            public Action[] getActions(Object target, Object sender)
            {
                return new Action[] { replaceRecordAction, replaceRecordWithDirSelectionAction, compareToRecordAction };
            }

            public void handleAction(Action action, Object sender, Object target)
            {
                if (action.equals(replaceRecordAction))
                {
                    initializeConfirmationWindow();
                }
                else if (action.equals(compareToRecordAction))
                {
                    compareToRecord();
                }
                else if (action.equals(replaceRecordWithDirSelectionAction))
                {
                    showDirSelectionWindow();
                }

            }
        });
        table.addListener(new ItemClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event)
            {
                if (event.getButton() == com.vaadin.event.MouseEvents.ClickEvent.BUTTON_RIGHT)
                {
                    table.select(event.getItemId());
                }
            }
        });
        table.setItemDescriptionGenerator(new ItemDescriptionGenerator()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String generateDescription(Component source, Object itemId, Object propertyId)
            {
                if (TEST_RESULT_LABEL.equals(propertyId))
                {
                    Table table = (Table) source;
                    String testResult = (String) table.getItem(itemId).getItemProperty(propertyId).getValue();
                    if (testResult.equals(LabelConstants.TEST_FAILED))
                    {
                        File file = (File) table.getItem(itemId).getItemProperty(TEST_LABEL).getValue();
                        String description = InfoFileManager.getInfo(file, InfoFileManager.FAILURE_REASON);
                        return description;
                    }
                }
                else if (ERROR_STATUS_LABEL.equals(propertyId))
                {
                    Table table = (Table) source;
                    String errorStatus = (String) table.getItem(itemId).getItemProperty(propertyId).getValue();
                    if (errorStatus.equals(LabelConstants.STATUS_ERROR))
                    {
                        File file = (File) table.getItem(itemId).getItemProperty(TEST_LABEL).getValue();
                        String filesWithErrros = InfoFileManager.getInfo(file, InfoFileManager.FILES_WITH_ERRORS);
                        String header = "Error in files:";
                        String footer = "\"" + COMPARE_TO_RECORD + "\" to see more details.";
                        String[] files = filesWithErrros.split(InfoFileManager.FILES_SEPARATOR);
                        String description = header + "<br>";
                        for (int i = 0; i < files.length; i++)
                        {
                            description += files[i] + "<br>";
                        }
                        description += footer;
                        return description;
                    }
                }
                else if (TEST_LABEL.equals(propertyId))
                {
                    Table table = (Table) source;
                    File test = (File) table.getItem(itemId).getItemProperty(propertyId).getValue();
                    return FileReader.getTestDescription(test);
                }
                return null;
            }
        });
    }

    protected void showDirSelectionWindow()
    {
        chooseDestinationDirForCopyWindow.show();
        addWindow(chooseDestinationDirForCopyWindow);
    }

    private void initializeConfirmationWindow()
    {
        confirmationWindow = new ConfirmationWindow();
        confirmationWindow.setInformationText("Do you want to replace record?");
        confirmationWindow.setResizable(false);
        confirmationWindow.addOkButtonListener(createOkButtonListener());
        addWindow(confirmationWindow);
    }

    private ClickListener createOkButtonListener()
    {
        return new ClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                copyFiles(null);

            }
        };
    }

    private void compareToRecord()
    {
        Set< ? > selected = (Set< ? >) table.getValue();
        Iterator< ? > iterator = selected.iterator();
        List<File[]> folderPairs = new ArrayList<File[]>();
        while (iterator.hasNext())
        {
            Object object = (Object) iterator.next();
            File compareFolder = (File) table.getItem(object).getItemProperty(TEST_LABEL).getValue();
            File recordFolder = new File(mainFileManager.getBasicTestFolderPath(compareFolder, null));
            folderPairs.add(new File[] { recordFolder, compareFolder });
        }
        switchToComparisonWindow(folderPairs);
    }

    private void switchToComparisonWindow(List<File[]> folderPairs)
    {
        if (folderPairs.isEmpty())
        {
            return;
        }
        // ComparisonFileTablePage comparisonTablePage = null;
        if (folderPairs.size() == 1)
        {
            ComparisonFileTablePage comparisonTablePage = (ComparisonFileTablePage) getApplication().getWindow(ComparisonFileTablePage.NAME_FILES);
            File recordFolder = folderPairs.get(0)[0];
            File compareFolder = folderPairs.get(0)[1];
            String profile = InfoFileManager.getInfo(compareFolder, InfoFileManager.COMPARE_ALGORITHM);
            if (profile != null)
            {
                profileManager.setActiveProfile(profile);
            }
            comparisonTablePage.createNewTable(recordFolder, compareFolder);
            open(new ExternalResource(comparisonTablePage.getURL()));
        }
        else
        {
            ComparisonTestTablePage comparisonTablePage = (ComparisonTestTablePage) getApplication().getWindow(ComparisonTestTablePage.NAME_TESTS);
            // TODO
            // different test can use different profiles
            File compareFolder = folderPairs.get(0)[1];
            String profile = InfoFileManager.getInfo(compareFolder, InfoFileManager.COMPARE_ALGORITHM);
            if (profile != null)
            {
                profileManager.setActiveProfile(profile);
            }
            comparisonTablePage.createNewTable(folderPairs);
            open(new ExternalResource(comparisonTablePage.getURL()));
        }
    }

    private void copyFiles(String airline)
    {
        Set< ? > selected = (Set< ? >) table.getValue();
        if (selected.isEmpty())
        {
            return;
        }
        Iterator< ? > iterator = selected.iterator();
        boolean succes = true;
        while (iterator.hasNext())
        {
            Object object = (Object) iterator.next();
            File compareFolder = (File) table.getItem(object).getItemProperty(TEST_LABEL).getValue();
            File basicFolder = new File(mainFileManager.getBasicTestFolderPath(compareFolder, airline));
            if (!CopyManager.copyFilesInDirs(compareFolder, basicFolder))
            {
                this.showNotification("Error during copying " + compareFolder.getName(), Notification.TYPE_ERROR_MESSAGE);
                succes = false;
            }
        }
        if (succes)
        {
            this.showNotification("Files have been copied!", Notification.TYPE_HUMANIZED_MESSAGE);
        }
    }

    private void refreshTable()
    {
        table.removeAllItems();
        if (run != null)
        {
            for (File testFolder : run.listFiles())
            {
                String status = null;
                String s = InfoFileManager.getInfo(testFolder, InfoFileManager.TEST_EXECUTION_STATUS);
                if (s != null)
                {
                    Boolean stat = new Boolean(s);
                    status = stat ? LabelConstants.TEST_PASSED : LabelConstants.TEST_FAILED;
                }
                String compareResult = InfoFileManager.getInfo(testFolder, InfoFileManager.COMPARE_RESULT);
                String filesWithErrors = InfoFileManager.getInfo(testFolder, InfoFileManager.FILES_WITH_ERRORS);
                String errorStatus = (filesWithErrors == null) ? LabelConstants.STATUS_OK : LabelConstants.STATUS_ERROR;
                String compareAlgoritm = InfoFileManager.getInfo(testFolder, InfoFileManager.COMPARE_ALGORITHM);
                Object[] object = { new TableFile(testFolder.getAbsolutePath()), status, compareResult.toUpperCase(), compareAlgoritm, errorStatus };
                table.addItem(object, null);
            }
        }
    }

    @Override
    public void refresh()
    {
        refreshTable();
    }

    public void setRun(File run)
    {
        this.run = run;
        runLabel.setValue("<h3>" + run.getName() + "</h3>");
    }
}
