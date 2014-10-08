package pages;

import helpers.TableRow;
import helpers.TableRowList;

import java.io.File;

import listeners.SaveFileListener;
import pages.components.FilesDiffTable;
import pages.components.NavigationPanel;
import pages.components.PagesMenuPanel;
import pages.components.customdiff.CustomDiffWindow;
import pages.components.customdiff.DiffTypeComboBox;
import spring.SpringBeanContainer;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

import constants.GuiMode;
import filehandlers.ProfileManager;

public class ShowFilesPage extends AbstractPage
{
    private static final long serialVersionUID = 1L;

    public static final String NAME = "showfiles";

    private FilesDiffTable filesDiffTable = new FilesDiffTable(this);
    private ComboBox selectViewComboBox = new ComboBox();
    private DiffTypeComboBox diffSet = new DiffTypeComboBox();
    private NavigationPanel navigationPanel = new NavigationPanel();
    private ProfileManager profileManager;
    private PagesMenuPanel menuPanel;
    private Panel bottomPanel = new Panel();
    private final CustomDiffWindow customDiffWindow;
    private File filepath1;
    private Label fileLabel = new Label();

    public ShowFilesPage()
    {
        customDiffWindow = SpringBeanContainer.getCustomDiffWindowBean();
        profileManager = SpringBeanContainer.getProfileManagerBean();
        setName(NAME);
        GridLayout grid = new GridLayout(1, 3);
        grid.setSizeFull();
        grid.setMargin(true);
        grid.setSpacing(true);
        menuPanel = new PagesMenuPanel(this);
        initializePanel();
        grid.addComponent(menuPanel, 0, 0);
        bottomPanel.getContent().setSizeFull();
        fileLabel.setContentMode(Label.CONTENT_XHTML);
        grid.addComponent(fileLabel, 0, 1);
        grid.addComponent(filesDiffTable.getTable(), 0, 2);
        grid.setRowExpandRatio(2, 1.0f);
        this.setContent(grid);

    }

    @Override
    public void refresh()
    {
        super.refresh();
        String profile = profileManager.getActiveProfileName();
        refreshDiffSetComboBox();
        profileManager.setActiveProfile(profile);
        diffSet.select(profile);
    }

    @Override
    public void refreshAfterClick()
    {
        TableRowList rowList = navigationPanel.getRowList();
        showFiles(rowList);
    }

    private String createTitle(File file)
    {
        if (file == null)
            return "";
        return file.getParentFile().getParentFile().getName() + File.separator + file.getParentFile().getName();
    }

    public void addBackButton(Window w)
    {
        menuPanel.addBackButton(new ExternalResource(w.getURL()));
    }

    public void showFiles(TableRowList list)
    {
        File filepath2;
        TableRow currentRow = list.getCurrent();
        filepath1 = currentRow.getFirstFile();
        filepath2 = currentRow.getSecondFile();
        String title1 = createTitle(filepath1);
        String title2 = createTitle(filepath2);
        filesDiffTable.updateWindows(filepath1, filepath2, title1, title2);
        filesDiffTable.switchMode((GuiMode) selectViewComboBox.getValue());
        String fileName = "";
        if (filepath1 != null)
        {
            fileName = filepath1.getName();
        }
        else if (filepath2 != null)
        {
            fileName = filepath2.getName();
        }
        fileLabel.setValue("<h3>File: " + fileName + "</h3>");
        navigationPanel.setTableInfo(list, this);
    }

    public void refreshDiffSetComboBox()
    {
        diffSet.setListenerEnabled(false);
        diffSet.removeAllItems();
        diffSet.addItem("Default");
        for (Profile p : profileManager.getProfileList())
        {
            diffSet.addItem(p.getName());
        }
        
        diffSet.setListenerEnabled(true);
        if (profileManager.getActiveProfileName().isEmpty())
        {
            diffSet.select("Default");
        }
        else
        {
            diffSet.select(profileManager.getActiveProfileName());
        }
    }

    private void initializePanel()
    {

        diffSet.setNullSelectionAllowed(false);
        diffSet.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                if (diffSet.isListenerEnabled()) {
                String profile = (String) diffSet.getValue();
                if (profile == null || profile.isEmpty())
                {
                    profile = "Default";
                }
                ShowFilesPage.this.profileManager.setActiveProfile(profile);
                filesDiffTable.switchMode(GuiMode.DIFF);
                }
            }
        });
        diffSet.setImmediate(true);
        refreshDiffSetComboBox();
        selectViewComboBox.addItem(GuiMode.EDITABLE);
        selectViewComboBox.addItem(GuiMode.DIFF);
        selectViewComboBox.setImmediate(true);
        selectViewComboBox.select(GuiMode.DIFF);
        selectViewComboBox.setNullSelectionAllowed(false);
        selectViewComboBox.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {

                if ((GuiMode) selectViewComboBox.getValue() == GuiMode.EDITABLE)
                {
                    diffSet.setEnabled(false);
                    filesDiffTable.switchMode((GuiMode) selectViewComboBox.getValue());
                }
                else
                {
                    diffSet.setEnabled(true);
                    filesDiffTable.switchMode(GuiMode.DIFF);
                }
            }

        });

        menuPanel.addComponent(selectViewComboBox);
        menuPanel.addComponent(diffSet);

        Button showCustomDiffWindowButton = new Button("Show custom diff editor");
        showCustomDiffWindowButton.addListener(new ChooseTagsListener());
        menuPanel.addComponent(showCustomDiffWindowButton);

        Button saveFileButton = new Button("Save Files");
        saveFileButton.addListener(new SaveFileListener(filesDiffTable));
        menuPanel.addComponent(saveFileButton);
        menuPanel.addComponent(navigationPanel, new Alignment(Bits.ALIGNMENT_RIGHT));

    }

    class ChooseTagsListener implements ClickListener
    {
        private static final long serialVersionUID = 1L;

        public void buttonClick(ClickEvent event)
        {
            showChooseTags();
        }
    }

    private void showChooseTags()
    {
        customDiffWindow.show(filepath1.getParentFile(), this);

    }

}
