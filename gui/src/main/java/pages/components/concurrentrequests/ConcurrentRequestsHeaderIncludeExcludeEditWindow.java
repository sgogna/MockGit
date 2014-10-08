/* Copyright 2009 EB2 International Limited */
package pages.components.concurrentrequests;

import java.io.File;

import pages.components.customdiff.DiffTypeComboBox;

import com.sabre.ssw.proxy.concurrent.managment.ConcurrentHeaderAttribute;
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

public class ConcurrentRequestsHeaderIncludeExcludeEditWindow extends Window
{

    private static final long serialVersionUID = 1L;
    private Button addButton;
    private DiffTypeComboBox paramComboBox = new DiffTypeComboBox("Parameter");
    private ConcurrentHeaderAttribute concurrentHeaderAttribute;

    public ConcurrentRequestsHeaderIncludeExcludeEditWindow()
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
        mainLayout.setWidth(400, Sizeable.UNITS_PIXELS);

        paramComboBox.setNewItemsAllowed(true);
        paramComboBox.setImmediate(true);
        paramComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        paramComboBox.setNullSelectionAllowed(false);

        paramComboBox.setWidth(100, UNITS_PERCENTAGE);

        mainLayout.addComponent(paramComboBox);

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
                ConcurrentRequestsHeaderIncludeExcludeEditWindow.this.getParent().removeWindow(ConcurrentRequestsHeaderIncludeExcludeEditWindow.this);
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

    public ConcurrentHeaderAttribute getHeaderTag()
    {
        ConcurrentHeaderAttribute attr = null;
        if (concurrentHeaderAttribute == null)
        {
            attr = new ConcurrentHeaderAttribute();
        }
        else
        {
            attr = concurrentHeaderAttribute;
        }
        attr.setAttribute((String) paramComboBox.getValue());
        return attr;
    }

    public void show(File f, ConcurrentHeaderAttribute attr)
    {
        concurrentHeaderAttribute = attr;

        paramComboBox.removeAllItems();
        if (f != null)
        {
            for (String text : FilesTagsManager.getOriginalHostList(f))
            {
                paramComboBox.addItem(text);
            }
        }
        
        paramComboBox.addItem("*");

        String value = "";
        if (attr == null)
        {
            value = "*";
        }
        else
        {
            value = attr.getAttribute();
        }
        if (!paramComboBox.containsId(value))
        {
            paramComboBox.addItem(value);
        }
        paramComboBox.select(value);
    }

    public boolean isNew()
    {
        return concurrentHeaderAttribute == null;
    }
}
