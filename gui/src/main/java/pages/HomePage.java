package pages;

import helpers.FileReader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import main.MainApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pages.components.GuiFileSystemContainer;
import pages.components.MenuPanel;
import pages.subwindows.MainPageDeleteTreeItems;

import com.sabre.ssw.proxy.defines.ProxyMode;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

import fileManagers.MainFileManager;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import org.apache.commons.io.FilenameUtils;
import pages.subwindows.InputWindow;

public class HomePage extends AbstractPage
{
    public static final String NAME = "Home";
    private static final Logger LOG = LoggerFactory.getLogger(HomePage.class);
    private static final long serialVersionUID = 1L;
    private MenuPanel menuPanel;
    private Tree fileTree;
    private FilesystemContainer filesystemContainer;
    private File fileRoot;

    private MainPageDeleteTreeItems confirmationWindow;

    public static final Action ACTION_NEW_ITEM = new Action("New file");
    public static final Action ACTION_NEW_DIR = new Action("New directory");
    public static final Action ACTION_DELETE = new Action("Delete");
    public static final Action COMPARE_DIRS = new Action("Compare dirs");

    public HomePage(String recDir)
    {
        setName(NAME);
        fileRoot = new File(recDir);
        filesystemContainer = new GuiFileSystemContainer(fileRoot);
        filesystemContainer.setFilter(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                if (name.startsWith("."))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        });

        fileTree = new Tree(recDir);
        menuPanel = new MenuPanel(recDir, fileTree, this);

        initializeFileTree();

        final VerticalLayout main = new VerticalLayout();
        this.setContent(main);
        main.setMargin(true);
        main.setSpacing(false);
        main.addComponent(menuPanel);
        main.addComponent(fileTree);
        fileTree.setItemCaptionPropertyId(FilesystemContainer.PROPERTY_NAME);
    }


    private void initializeFileTree()
    {
        fileTree.setContainerDataSource(filesystemContainer);
        fileTree.setMultiSelect(true);
        fileTree.addListener(new ItemClickEvent.ItemClickListener()
        {
            private static final long serialVersionUID = 1L;

            public void itemClick(ItemClickEvent event)
            {

                if (event.isDoubleClick())
                {
                    Set< ? > set = (Set< ? >) fileTree.getValue();
                    for (Object i : set)
                    {
                        File file = new File(i.toString());
                        if (file.isFile())
                        {
                            ViewFilePage viewFilePage = (ViewFilePage) ((MainApplication) getApplication()).getWindowWithoutRefresh(ViewFilePage.NAME);
                            viewFilePage.openFile(file);
                            open(new ExternalResource(viewFilePage.getURL()));
                        }
                        if (MainFileManager.isRunInCompareFolder(file) || MainFileManager.isRunInReplayFolder(file))
                        {
                            TestStatusPage testStatusPage = (TestStatusPage) ((MainApplication) getApplication()).getWindowWithoutRefresh(TestStatusPage.NAME);
                            testStatusPage.setRun(file);
                            open(new ExternalResource(testStatusPage.getURL()));
                        }
                    }
                }
            }
        });
        
        fileTree.addActionHandler(new Handler()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void handleAction(Action action, Object sender, Object target)
            {
                if (action.equals(ACTION_DELETE))
                {
                    initializeConfirmationWindow(target);
                }

                if (action.equals(COMPARE_DIRS))
                {
                    menuPanel.compareDirsAction();
                }
                if (action.equals(ACTION_NEW_ITEM)){
                   if(target instanceof File){
                       createNewFile((File)target);
                   }
                }
                if (action.equals(ACTION_NEW_DIR)){
                   if(target instanceof File){
                       createDirectory((File)target);
                   }
                }
            }

            @Override
            public Action[] getActions(Object target, Object sender)
            {

                if (target != null)
                {
                    File clicked = (File) target;
                    List<Action> actions = new ArrayList<Action>();
                    if (isTestFolder(clicked))
                    {
                        actions.add(COMPARE_DIRS);
                    }

                    if (clicked.isDirectory())
                    {
                        actions.add(ACTION_NEW_ITEM);
                        actions.add(ACTION_NEW_DIR);
                    }
                        actions.add(ACTION_DELETE);

                    return actions.toArray(new Action[0]);
                }
                return null;
            }
        });
        
        fileTree.setItemDescriptionGenerator(new ItemDescriptionGenerator()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public String generateDescription(Component source, Object itemId, Object propertyId)
            {
                if (itemId == null)
                {
                    return null;
                }
                File file = (File) itemId;
                if (isTestFolder(file))
                {
                    return FileReader.getTestDescription(file);
                }
                return null;
            }
        });

    }

    private boolean isTestFolder(File f)
    {
        File lvl1 = f.getParentFile();
        if (lvl1 != null)
        {
            if (lvl1.getName().equals(MainFileManager.BASE))
            {
                return true;
            }
            File lvl2 = lvl1.getParentFile();
            if (lvl2 != null)
            {
                if (lvl2.getName().equals(ProxyMode.REPLAY.toString()) || lvl2.getName().equals(ProxyMode.COMPARE.toString()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void initializeConfirmationWindow(Object target)
    {
        confirmationWindow = new MainPageDeleteTreeItems();
        confirmationWindow.setResizable(false);
        confirmationWindow.addOkButtonListener(createOkButtonListener());
        confirmationWindow.show((Set< ? >) fileTree.getValue());
        addWindow(confirmationWindow);
    }
    
    private void createNewFile(final File parentFile)
    {
        final InputWindow inputWindow = new InputWindow("Filename:");
        inputWindow.setResizable(false);
        inputWindow.addOkButtonListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                    String filename = FilenameUtils.concat(parentFile.getPath(), inputWindow.getInputText());
                    try {
                        new File(filename).createNewFile();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                fileTree.requestRepaint();
            }
        });
        addWindow(inputWindow);
    }
    
    private void createDirectory(final File parentFile){
        final InputWindow inputWindow = new InputWindow("Directory:");
        inputWindow.setResizable(false);
        inputWindow.addOkButtonListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String filename = FilenameUtils.concat(parentFile.getPath(), inputWindow.getInputText());
//                    try {
                new File(filename).mkdirs();
//                    } catch (IOException ex) {
//                        java.util.logging.Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                fileTree.requestRepaint();
            }
        });
        addWindow(inputWindow);
    }

    private ClickListener createOkButtonListener()
    {
        return new ClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                Set<?> selectedItems = (Set< ? >) fileTree.getValue();
                for (Object o: selectedItems) {
                    fileTree.removeItem(o);
                }

            }
        };
    }

    public static boolean hasChildrenFolder(File dir)
    {
        for (File f : dir.listFiles())
        {
            if (!f.getName().startsWith(".") && f.isDirectory())
                return true;
        }
        return false;
    }

    public void switchToCompareRunsPage(String recDir, String runId)
    {
        ComparisonTestTablePage comparisonTestsTablePage = (ComparisonTestTablePage) ((MainApplication) getApplication()).getWindowWithoutRefresh(ComparisonTestTablePage.NAME_TESTS);
        comparisonTestsTablePage.createNewTable(new File(recDir), runId);
        open(new ExternalResource(comparisonTestsTablePage.getURL()));
    }

    public void switchToWindow(AbstractPage c)
    {
        open(new ExternalResource(c.getURL()));
    }

    public void switchToComparisonWindow(File file1, File file2)
    {
        ComparisonFileTablePage comparisonFilesTablePage = (ComparisonFileTablePage) ((MainApplication) getApplication()).getWindowWithoutRefresh(ComparisonFileTablePage.NAME_FILES);
        comparisonFilesTablePage.createNewTable(file1, file2);
        comparisonFilesTablePage.disableNavPanel();
        open(new ExternalResource(comparisonFilesTablePage.getURL()));
    }

    public void switchToComparisonWindow(Iterator<File> iter)
    {
        switchToComparisonWindow(iter.next(), iter.next());
    }

    @Override
    public void refresh()
    {
        menuPanel.refresh();
    }
    
    public Collection getSelectedItems(){
        Set<?> selectedItems = (Set< ? >) fileTree.getValue();
        return selectedItems;
    }
}
