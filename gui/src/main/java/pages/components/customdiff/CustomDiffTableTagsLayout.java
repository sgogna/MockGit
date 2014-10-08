/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.sabre.ssw.proxy.compare.profile.TagRecord;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class CustomDiffTableTagsLayout extends VerticalLayout
{

    private static final long serialVersionUID = 1L;
    private CustomDiffWindow customDiffWindow;
    private Button addNewTagButton;
    private Table tableTags = new Table();
    private CustomDiffEditTagRecordWindow editWindow;
    private CheckBox skipNamespace;

    public CheckBox getSkipNamespace()
    {
        return skipNamespace;
    }

    public CustomDiffTableTagsLayout(CustomDiffWindow customDiffWindow)
    {
        this.customDiffWindow = customDiffWindow;
        this.editWindow = new CustomDiffEditTagRecordWindow(customDiffWindow, true);
        initialize();
    }

    private void initialize()
    {
        prepareTableTags();
        // VerticalLayout tableTagsPanelLayout = new VerticalLayout();

        this.addComponent(tableTags);
        HorizontalLayout optionPanel = prepareProfileOptionsPanel();
        this.addComponent(optionPanel);
        this.setSizeFull();
        this.setExpandRatio(tableTags, 1.0f);
    }

    private HorizontalLayout prepareProfileOptionsPanel()
    {

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        addNewTagButton = new Button("Add new");
        addNewTagButton.setImmediate(true);
        addNewTagButton.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                addNewTag();
            }
        });

        skipNamespace = new CheckBox("Skip namespace");
        skipNamespace.setValue(true);

        horizontalLayout.addComponent(addNewTagButton);
        horizontalLayout.addComponent(skipNamespace);
        horizontalLayout.setWidth(100, UNITS_PERCENTAGE);
        return horizontalLayout;
    }

    private void prepareTableTags()
    {
        tableTags.setSizeFull();
        tableTags.addContainerProperty("FILE", String.class, null);
        tableTags.addContainerProperty("TAG", String.class, null);
        tableTags.addContainerProperty("ATTRIBUTE", String.class, null);
        tableTags.addContainerProperty("DELETE", Button.class, null);
        tableTags.setImmediate(true);
        tableTags.setSelectable(true);
        tableTags.addListener(new ItemClickEvent.ItemClickListener()
        {

            private static final long serialVersionUID = 1L;

            public void itemClick(ItemClickEvent event)
            {
                if (event.isDoubleClick())
                {
                    try
                    {
                        customDiffWindow.getParent().addWindow(editWindow);
                        boolean skip = customDiffWindow.getActiveProfile() != null ? customDiffWindow.getActiveProfile().isSkipNameSpace() : false;
                        editWindow.show((TagRecord) event.getItemId(), "Edit", skip, customDiffWindow.getContext(), customDiffWindow.getActiveProfile());
                    }
                    catch (Exception e)
                    {

                    }

                }
            }
        });
    }

    private void addNewTag()
    {
        try
        {
            customDiffWindow.getParent().addWindow(editWindow);
            TagRecord record = new TagRecord("*", true, "*", "*", false, false, false);
            boolean skip = customDiffWindow.getActiveProfile() != null ? customDiffWindow.getActiveProfile().isSkipNameSpace() : false;
            editWindow.show(record, "New", skip, customDiffWindow.getContext(), customDiffWindow.getActiveProfile());
        }
        catch (Exception e)
        {
        }
    }

    public void refreshTableTags()
    {
        Profile p = customDiffWindow.getActiveProfile();
        tableTags.removeAllItems();

        if (p == null)
        {
            return;
        }

        for (final TagRecord record : p.getTagList())
        {
            Button b = new Button("Delete");
            // b.setHeight("10px");
            b.setStyleName(BaseTheme.BUTTON_LINK);
            b.setIcon(new ThemeResource("icons/delete.gif"));
            b.addListener(new ClickListener()
            {

                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(ClickEvent event)
                {
                    customDiffWindow.removeTagFromProfile(record);
                    refreshTableTags();
                }
            });
            Object[] objects = new Object[] { record.getFile(), record.getTag(), record.getAttribute(), b };
            tableTags.addItem(objects, record);
        }
    }

}
