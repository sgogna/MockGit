/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import java.io.File;
import java.util.Map;

import spring.SpringBeanContainer;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.sabre.ssw.proxy.compare.profile.TagRecord;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import filehandlers.FilesTagsManager;

public class CustomDiffEditTagRecordWindow extends Window
{

    private class MyListener implements ClickListener
    {
        private static final long serialVersionUID = 1L;
        private TagRecord record;

        @Override
        public void buttonClick(ClickEvent event)
        {
            String file = (String) customDiffFilesComboBox.getFileComboBox().getValue();
            record.setFile(file.isEmpty() ? "*" : file);
            String tag = (String) tagComboBox.getValue();
            record.setTag(tag.isEmpty() ? "*" : tag);
            String param = (String) atrComboBox.getValue();
            record.setAttribute(param.isEmpty() ? "*" : param);
            boolean fileCheckBox = customDiffFilesComboBox.getFileRegexCheckBox().booleanValue();
            record.setFileCheckBoxValue(fileCheckBox);
            boolean tagCheckBox = tagRegexCheckBox.booleanValue();
            record.setTagCheckBoxValue(tagCheckBox);
            boolean attributeCheckBox = atrRegexCheckBox.booleanValue();
            record.setAttributeCheckBoxValue(attributeCheckBox);
            record.setCommand(customDiffFilesComboBox.isCommandChecked());

            CustomDiffEditTagRecordWindow.this.customDiffWindow.saveTagRecord(record);
            CustomDiffEditTagRecordWindow.this.customDiffWindow.getParent().removeWindow(CustomDiffEditTagRecordWindow.this);
        }

        public void setRecord(TagRecord record)
        {
            this.record = record;
        }
    }

    private DiffTypeComboBox tagComboBox = new DiffTypeComboBox("Tag");
    private DiffTypeComboBox atrComboBox = new DiffTypeComboBox("Attribute");
    private CustomDiffFilesComboBox customDiffFilesComboBox;

    private CheckBoxListener tagCheckBoxListener = new CheckBoxListener(tagComboBox);
    private CheckBox tagRegexCheckBox = new CheckBox("Regex", tagCheckBoxListener);
    private CheckBoxListener atrCheckBoxListener = new CheckBoxListener(atrComboBox);
    private CheckBox atrRegexCheckBox = new CheckBox("Regex", atrCheckBoxListener);

    private CustomDiffWindow customDiffWindow;
    private static final long serialVersionUID = 1L;
    private Button addButton;
    private MyListener listener = new MyListener();
    private FilesTagsManager filesTagsManager;
    private boolean isSkipNameSpace = false;

    private Map<String, String> tagMap;
    private VerticalLayout mainLayout;
    private  boolean windowMode;

    public CustomDiffEditTagRecordWindow(CustomDiffWindow customDiffWindow, boolean windowMode)
    {
        this.setModal(true);
        this.setResizable(false);
        this.customDiffWindow = customDiffWindow;
        this.customDiffFilesComboBox = new CustomDiffFilesComboBox();
        this.windowMode = windowMode;
        filesTagsManager = SpringBeanContainer.getFileTagsManager();
        initializeComponents();
    }

    private void initializeComponents()
    {

        mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setSizeUndefined();
        mainLayout.setWidth(600, Sizeable.UNITS_PIXELS);

        customDiffFilesComboBox.getFileComboBox().addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                if (customDiffFilesComboBox.getFileComboBox().isListenerEnabled())
                {
                    refreshTagComboBox((String) (event.getProperty().getValue()));
                }
            }
        });

        tagComboBox.setNewItemsAllowed(true);
        tagComboBox.setImmediate(true);
        tagComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        tagComboBox.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                if (tagComboBox.isListenerEnabled())
                {
                    refreshAttributesComboBox((String) (event.getProperty().getValue()));
                }
            }
        });
        tagComboBox.setNullSelectionAllowed(false);
        atrComboBox.setNewItemsAllowed(true);
        atrComboBox.setImmediate(true);
        atrComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        atrComboBox.setNullSelectionAllowed(false);

        HorizontalLayout tagHorizontalLayout = new HorizontalLayout();
        tagHorizontalLayout.addComponent(tagComboBox);
        tagHorizontalLayout.setExpandRatio(tagComboBox, 1.0f);
        tagRegexCheckBox.setImmediate(true);
        tagHorizontalLayout.addComponent(tagRegexCheckBox);
        tagHorizontalLayout.setComponentAlignment(tagRegexCheckBox, Alignment.BOTTOM_RIGHT);
        tagHorizontalLayout.setMargin(false);

        HorizontalLayout atrHorizontalLayout = new HorizontalLayout();
        atrHorizontalLayout.addComponent(atrComboBox);
        atrHorizontalLayout.setExpandRatio(atrComboBox, 1.0f);
        atrRegexCheckBox.setImmediate(true);
        atrHorizontalLayout.addComponent(atrRegexCheckBox);
        atrHorizontalLayout.setComponentAlignment(atrRegexCheckBox, Alignment.BOTTOM_RIGHT);
        atrHorizontalLayout.setMargin(false);

        tagComboBox.setWidth(100, UNITS_PERCENTAGE);
        atrComboBox.setWidth(100, UNITS_PERCENTAGE);

        tagHorizontalLayout.setWidth(100, UNITS_PERCENTAGE);
        atrHorizontalLayout.setWidth(100, UNITS_PERCENTAGE);

        mainLayout.addComponent(customDiffFilesComboBox.getFileVerticalLayout());
        mainLayout.addComponent(tagHorizontalLayout);
        mainLayout.addComponent(atrHorizontalLayout);

        if (windowMode) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setMargin(true);
        Button cancel = new Button("Cancel");
        cancel.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                CustomDiffEditTagRecordWindow.this.getParent().removeWindow(CustomDiffEditTagRecordWindow.this);
            }
        });
        addButton = new Button("Add");
        addButton.addListener(listener);
        cancel.setWidth(80, UNITS_PIXELS);
        addButton.setWidth(80, UNITS_PIXELS);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.addComponent(addButton);
        mainLayout.addComponent(horizontalLayout);
        mainLayout.setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);
        this.setContent(mainLayout);
        }
    }

    private void refreshAttributesComboBox(String tag)
    {
        String value = (String) atrComboBox.getValue();
        atrComboBox.removeAllItems();
        for (String s : filesTagsManager.getAttributes(tagMap, tag, atrRegexCheckBox.booleanValue()))
        {
            atrComboBox.addItem(s);
        }
        if (value != null)
        {
            if (!atrComboBox.containsId(value))
            {
                atrComboBox.addItem(value);
            }
            atrComboBox.setValue(value);
        }
    }

    private void refreshTagComboBox(String name)
    {
        String value = (String) tagComboBox.getValue();
        tagComboBox.setListenerEnabled(false);
        tagComboBox.removeAllItems();
        tagMap = filesTagsManager.getTags(customDiffFilesComboBox.getFilesList(), name, tagRegexCheckBox.booleanValue(), customDiffFilesComboBox.isCommandChecked(), isSkipNameSpace);
        for (String s : tagMap.keySet())
        {
            tagComboBox.addItem(s);
        }
        tagComboBox.setListenerEnabled(true);
        if (value != null)
        {
            if (!tagComboBox.containsId(value))
            {
                tagComboBox.addItem(value);
            }
            tagComboBox.setValue(value);
        }
    }

    public void show(final TagRecord record, String caption, boolean isSkipNameSpace, File context, Profile profile)
    {
        this.setCaption(caption);
        this.isSkipNameSpace = isSkipNameSpace;

        customDiffFilesComboBox.show(record.getFile(), record.getFileCheckBoxValue(), record.isCommand(), context, profile);
        tagRegexCheckBox.setValue(record.getTagCheckBoxValue());
        refreshTagComboBox(record.getFile());
        if (!tagComboBox.containsId(record.getTag()))
            tagComboBox.addItem(record.getTag());
        tagComboBox.setValue(record.getTag());
        atrRegexCheckBox.setValue(record.getAttributeCheckBoxValue());
        refreshAttributesComboBox(record.getTag());
        if (!atrComboBox.containsId(record.getAttribute()))
            atrComboBox.addItem(record.getAttribute());
        atrComboBox.select(record.getAttribute());

        listener.setRecord(record);
    }
    
    public VerticalLayout getMainLayout()
    {
        return mainLayout;
    }

    public DiffTypeComboBox getTagComboBox()
    {
        return tagComboBox;
    }

    public DiffTypeComboBox getAtrComboBox()
    {
        return atrComboBox;
    }

    public CheckBox getTagRegexCheckBox()
    {
        return tagRegexCheckBox;
    }

    public CheckBox getAtrRegexCheckBox()
    {
        return atrRegexCheckBox;
    }
    
    
    public CheckBox getFileRegexCheckBox()
    {
        return customDiffFilesComboBox.getFileRegexCheckBox();
    }
    
    public DiffTypeComboBox getFileComboBox()
    {
        return customDiffFilesComboBox.getFileComboBox();
    }

    
    
}
