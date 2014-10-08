/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import java.io.File;
import java.util.List;

import spring.SpringBeanContainer;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

import filehandlers.FileNamePair;
import filehandlers.FilesTagsManager;

public class CustomDiffFilesComboBox
{
    private DiffTypeComboBox fileComboBox = new DiffTypeComboBox();
    private MenuItem file;
    private MenuItem command;
    private CheckBoxListener fileCheckBoxListener = new CheckBoxListener(fileComboBox);
    private CheckBox fileRegexCheckBox = new CheckBox("Regex", fileCheckBoxListener);
    private CheckBox filesNotNumberCheckBox = new CheckBox("Show *");
    private VerticalLayout fileVerticalLayout;
    private File context;
    private Profile activeProfile;
    private List<FileNamePair> filesList;
    private boolean commandChecked = true;
    private FilesTagsManager filesTagsManager;

    public CustomDiffFilesComboBox()
    {
        initializeComponents();
        filesTagsManager = SpringBeanContainer.getFileTagsManager();
    }

    private void initializeComponents()
    {
        MenuBar fileCommandChooser = new MenuBar();
        fileCommandChooser.setStyleName("clear");
        file = fileCommandChooser.addItem("Files", new Command()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void menuSelected(MenuItem selectedItem)
            {
                setFiles();
            }
        });
        command = fileCommandChooser.addItem("Commands", new Command()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void menuSelected(MenuItem selectedItem)
            {
                setCommand();
            }
        });
        file.setStyleName("normal-style");
        command.setStyleName("normal-style");

        HorizontalLayout fileHorizontalLayout = new HorizontalLayout();
        fileHorizontalLayout.addComponent(fileComboBox);
        fileHorizontalLayout.setExpandRatio(fileComboBox, 1.0f);
        fileRegexCheckBox.setImmediate(true);
        fileHorizontalLayout.addComponent(fileRegexCheckBox);
        fileHorizontalLayout.addComponent(filesNotNumberCheckBox);
        fileHorizontalLayout.setComponentAlignment(fileRegexCheckBox, Alignment.BOTTOM_RIGHT);
        fileHorizontalLayout.setComponentAlignment(filesNotNumberCheckBox, Alignment.BOTTOM_RIGHT);
        fileHorizontalLayout.setMargin(false);
        fileHorizontalLayout.setWidth(100, Sizeable.UNITS_PERCENTAGE);

        fileVerticalLayout = new VerticalLayout();
        fileVerticalLayout.setMargin(false);
        fileVerticalLayout.setSpacing(false);
        fileVerticalLayout.addComponent(fileCommandChooser);
        fileVerticalLayout.addComponent(fileHorizontalLayout);

        fileComboBox.setNewItemsAllowed(true);
        fileComboBox.setImmediate(true);
        fileComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);

        fileComboBox.setNullSelectionAllowed(false);
        fileComboBox.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        fileVerticalLayout.setWidth(100, Sizeable.UNITS_PERCENTAGE);

        filesNotNumberCheckBox.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                setNewFilesToComboBox((Boolean) event.getProperty().getValue());
            }
        });

    }

    private void setCommand()
    {
        command.setStyleName("active-style");
        file.setStyleName("normal-style");
        commandChecked = true;
        filesNotNumberCheckBox.setVisible(false);
        filesList = filesTagsManager.getCommands(context, activeProfile.getIncludeList(), activeProfile.getExcludeList());
        refreshFilesComboBox();
    }

    private void setFiles()
    {
        file.setStyleName("active-style");
        command.setStyleName("normal-style");
        commandChecked = false;
        filesNotNumberCheckBox.setVisible(true);
        filesNotNumberCheckBox.setValue(true);
        setNewFilesToComboBox(true);
    }

    public VerticalLayout getFileVerticalLayout()
    {
        return fileVerticalLayout;
    }

    public DiffTypeComboBox getFileComboBox()
    {
        return fileComboBox;
    }

    public CheckBox getFileRegexCheckBox()
    {
        return fileRegexCheckBox;
    }

    private void refreshFilesComboBox()
    {
        String value = (String) fileComboBox.getValue();
        fileComboBox.setListenerEnabled(false);
        fileComboBox.removeAllItems();
        for (FileNamePair s : filesList)
        {
            fileComboBox.addItem(s.getName());
        }
        fileComboBox.setListenerEnabled(true);
        if (value != null)
        {
            if (!fileComboBox.containsId(value))
            {
                fileComboBox.addItem(value);
            }
            fileComboBox.setValue(value);
        }
        else
        {
            fileComboBox.setValue("*");
        }
    }

    private void setNewFilesToComboBox(boolean noNumber)
    {
        filesList = filesTagsManager.getFiles(context, activeProfile.getIncludeList(), activeProfile.getExcludeList(), noNumber);
        refreshFilesComboBox();
    }

    public void show(String value, boolean regex, boolean command, File context, Profile profile)
    {
        this.activeProfile = profile;
        this.context = context;
        fileRegexCheckBox.setValue(regex);
        if (command)
        {
            setCommand();
        }
        else
        {
            setFiles();
        }

        if (!fileComboBox.containsId(value))
            fileComboBox.addItem(value);
        fileComboBox.select(value);
    }

    public List<FileNamePair> getFilesList()
    {
        return filesList;
    }

    public boolean isCommandChecked()
    {
        return commandChecked;
    }

    public FilesTagsManager getFilesTagsManager()
    {
        return filesTagsManager;
    }

    public void setFilesTagsManager(FilesTagsManager filesTagsManager)
    {
        this.filesTagsManager = filesTagsManager;
    }

}
