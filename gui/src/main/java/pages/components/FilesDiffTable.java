package pages.components;

import helpers.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.w3c.dom.Document;

import pages.ShowFilesPage;
import pages.components.customdiff.CustomDiffQuickAddWindow;
import spring.SpringBeanContainer;
import util.Pair;

import com.sabre.ssw.proxy.compare.Comparator;
import com.vaadin.event.Action;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;

import constants.GuiMode;
import fileManagers.MainFileManager;
import filehandlers.ProfileManager;
import formatters.CustomDiff;
import formatters.TextDiffSelector;
import formatters.XmlFormatter;

public class FilesDiffTable
{
    private TextArea left;
    private Label leftLabel = new Label();
    private TextArea right;
    private Label rightLabel = new Label();
    private Panel leftPanel = new Panel();
    private Panel rightPanel = new Panel();
    private File file1;
    private File file2;
    private Table compareTable = new Table();
    private static String LEFT_COLUMN_ID = "leftColumn";
    private static String RIGHT_COLUMN_ID = "rightColumn";
    private final Action quickAdd = new Action("Quick add tag/attribute...");
    private CustomDiffQuickAddWindow quickAddWindow;
    private ShowFilesPage showFilesPage;

    private static final String NOTHING_TO_SHOW = "Nothing to show";

    public FilesDiffTable(final ShowFilesPage showFilesPage)
    {
        this.showFilesPage = showFilesPage;
        quickAddWindow = SpringBeanContainer.getCustomDiffQuickAddWindow();
        left = new TextArea();
        left.setSizeFull();
        leftPanel.setSizeUndefined();
        leftPanel.addComponent(left);

        right = new TextArea();
        rightPanel.setSizeUndefined();
        rightPanel.addComponent(right);

        compareTable.setSizeFull();
        compareTable.setEditable(true);
        compareTable.addContainerProperty(LEFT_COLUMN_ID, Panel.class, null);
        compareTable.addContainerProperty(RIGHT_COLUMN_ID, Panel.class, null);
        compareTable.setSelectable(true);
        Object[] obj = new Object[] { leftPanel, rightPanel };
        compareTable.addItem(obj, 1);
        compareTable.setColumnExpandRatio(LEFT_COLUMN_ID, 1);
        compareTable.setColumnExpandRatio(RIGHT_COLUMN_ID, 1);
        leftLabel.setContentMode(Label.CONTENT_XHTML);
        rightLabel.setContentMode(Label.CONTENT_XHTML);

        leftPanel.addComponent(leftLabel);
        rightPanel.addComponent(rightLabel);

        compareTable.addActionHandler(new Action.Handler()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void handleAction(Action action, Object sender, Object target)
            {
                if (action.equals(quickAdd))
                {
                    ProfileManager manager = SpringBeanContainer.getProfileManagerBean();
                    if (manager.getActiveProfile() == null)
                    {
                        showFilesPage.showNotification("Can't add skipped tag for this profile.");
                    }
                    else
                    {
                        showQuickAddWindow();
                    }
                }
            }

            @Override
            public Action[] getActions(Object target, Object sender)
            {
                return new Action[] { quickAdd };
            }
        });

    }

    public void switchMode(GuiMode mode)
    {

        switch (mode)
        {
            case EDITABLE:

                leftPanel.removeAllComponents();
                rightPanel.removeAllComponents();
                leftPanel.setWidth("100%");
                leftPanel.addComponent(left);
                rightPanel.setWidth("100%");
                rightPanel.addComponent(right);

                break;
            case DIFF:
                rightPanel.setSizeUndefined();
                leftPanel.setSizeUndefined();
                setAndUpdateLabels();
                break;

        }
    }

    public Panel getLeftPanel()
    {
        return leftPanel;
    }

    public Panel getRightPanel()
    {

        return rightPanel;
    }

    public void updateWindows(File filepath1, File filepath2,
            String title1, String title2)
    {

        this.file1 = filepath1;
        this.file2 = filepath2;

        setText(filepath1, leftPanel, left, title1, LEFT_COLUMN_ID);
        setText(filepath2, rightPanel, right, title2, RIGHT_COLUMN_ID);
    }

    private void setAndUpdateLabels()
    {
        updateLabels();
        leftPanel.removeAllComponents();
        leftPanel.addComponent(leftLabel);
        rightPanel.removeAllComponents();
        rightPanel.addComponent(rightLabel);

    }

    private void updateLabels()
    {
        Comparator comparator = new Comparator();
        if (file1 != null && SpringBeanContainer.getSessionConfiguration().NEW_DIFF)
        {
            if (comparator.isXmlFile(file1))
            {
                ProfileManager profileManager = SpringBeanContainer.getProfileManagerBean();

                Pair<Document> pair = comparator.compareFilesWithDiffSelection(file1, file2, profileManager.getActiveProfile());
                if (pair.getValue1() == null && pair.getValue2() == null)
                {
                    Pair<String> pair1 = TextDiffSelector.selectDiff(MainFileManager.readFile(file1), MainFileManager.readFile(file2));
                    leftLabel.setValue(pair1.getValue1());
                    rightLabel.setValue(pair1.getValue2());
                }
                else if (pair.getValue1() == null)
                {
                    leftLabel.setValue(MainFileManager.readFile(file1));
                    rightLabel.setValue(XmlFormatter.formatXML(pair.getValue2()));
                }
                else if (pair.getValue2() == null)
                {
                    leftLabel.setValue(XmlFormatter.formatXML(pair.getValue1()));
                    rightLabel.setValue(MainFileManager.readFile(file2));
                } else {
                    leftLabel.setValue(XmlFormatter.formatXML(pair.getValue1()));
                    rightLabel.setValue(XmlFormatter.formatXML(pair.getValue2()));
                }

            }
            else
            {
                Pair<String> pair = TextDiffSelector.selectDiff(MainFileManager.readFile(file1), MainFileManager.readFile(file2));
                leftLabel.setValue(pair.getValue1());
                rightLabel.setValue(pair.getValue2());
            }
        }
        else
        {
            leftLabel.setValue(CustomDiff.getDiffText(file1, file2));
            rightLabel.setValue(CustomDiff.getDiffText(file2, file1));
        }
    }

    private void setText(File file, Panel panel, TextArea textArea,
            String title, String column)
    {
        if (file == null || file.isDirectory())
        {
            textArea.setValue(title);
            compareTable.setColumnHeader(column, NOTHING_TO_SHOW);
            return;
        }
        String text = XmlFormatter.format(FileReader.readFile(file));
        textArea.setValue(text);
        textArea.setWordwrap(false);
        textArea.setRows(text.split("\n").length + 1);
        textArea.setSizeFull();
        compareTable.setColumnHeader(column, title);
    }

    public void saveFiles()
    {
        saveFile(file1, left);
        saveFile(file2, right);
    }

    private void saveFile(File dir, TextArea area)
    {
        FileWriter out;
        if (dir != null)
        {
            try
            {
                out = new FileWriter(dir);
                out.write(area.toString());
                out.close();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Table getTable()
    {
        return compareTable;
    }

    private void showQuickAddWindow()
    {
        quickAddWindow.show(file1);
        try
        {
            showFilesPage.getWindow().addWindow(quickAddWindow);
        }
        catch (Exception e)
        {
        }
    }

}
