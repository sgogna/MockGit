/* Copyright 2009 EB2 International Limited */
package pages.components.concurrentrequests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pages.components.customdiff.DiffTypeComboBox;
import spring.SpringBeanContainer;

import com.sabre.ssw.proxy.concurrent.managment.ConcurrentXMLTag;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import filehandlers.FilesTagsManager;

public class ConcurrentRequestsExcludeTagEditWindow extends Window
{

    private static final long serialVersionUID = 1L;
    private Button addButton;
    private DiffTypeComboBox tagComboBox = new DiffTypeComboBox("Tag");
    private DiffTypeComboBox atrComboBox = new DiffTypeComboBox("Attribute");
    private ConcurrentXMLTag concurrentXMLTag;
    private Map<String, String> tagMap;

    public ConcurrentRequestsExcludeTagEditWindow()
    {
        this.setModal(true);
        this.setResizable(false);
        initializeComponents();
    }

    private void initializeComponents()
    {

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setSizeUndefined();
        mainLayout.setWidth(600, Sizeable.UNITS_PIXELS);

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

        tagComboBox.setWidth(100, UNITS_PERCENTAGE);
        atrComboBox.setWidth(100, UNITS_PERCENTAGE);

        mainLayout.addComponent(tagComboBox);
        mainLayout.addComponent(atrComboBox);

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
                ConcurrentRequestsExcludeTagEditWindow.this.getParent().removeWindow(ConcurrentRequestsExcludeTagEditWindow.this);
            }
        });
        addButton = new Button("Add");
        cancel.setWidth(80, UNITS_PIXELS);
        addButton.setWidth(80, UNITS_PIXELS);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.addComponent(addButton);
        mainLayout.addComponent(horizontalLayout);
        mainLayout.setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);

        this.setContent(mainLayout);
    }

    public void addButtonListener(ClickListener clickListener)
    {
        addButton.addListener(clickListener);
    }

    public ConcurrentXMLTag getExcludeTag()
    {
        ConcurrentXMLTag tag = null;
        if (concurrentXMLTag == null)
        {
            tag = new ConcurrentXMLTag();
        }
        else
        {
            tag = concurrentXMLTag;
        }
        tag.setAttributes((String) atrComboBox.getValue());
        tag.setTag((String) tagComboBox.getValue());
        return tag;
    }

    public void show(File selectedFile, ConcurrentXMLTag tag)
    {
        String value = (String) tagComboBox.getValue();
        tagComboBox.setListenerEnabled(false);
        tagComboBox.removeAllItems();
        if (selectedFile != null)
        {
            this.tagMap = SpringBeanContainer.getFileTagsManager().getTagList(selectedFile, true);
            for (String s : tagMap.keySet())
            {
                tagComboBox.addItem(s);
            }
        }
        else
        {
            this.tagMap = new HashMap<String, String>();
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
        concurrentXMLTag = tag;

        String tagValue = "";
        String attrValue = "";
        if (tag == null)
        {
            attrValue = "*";
            tagValue = "*";
        }
        else
        {
            tagValue = tag.getTag();
            attrValue = tag.getAttributes();
        }
        if (!tagComboBox.containsId(tagValue))
            tagComboBox.addItem(tagValue);
        tagComboBox.select(tagValue);
        refreshAttributesComboBox(tagValue);
        if (!atrComboBox.containsId(attrValue))
            atrComboBox.addItem(attrValue);
        atrComboBox.select(attrValue);

    }

    public boolean isNew()
    {
        return concurrentXMLTag == null;
    }

    private void refreshAttributesComboBox(String tag)
    {
        String value = (String) atrComboBox.getValue();
        atrComboBox.removeAllItems();
        FilesTagsManager filesTagsManager = SpringBeanContainer.getFileTagsManager();
        for (String s : filesTagsManager.getAttributes(tagMap, tag, false))
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
}
