/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import com.sabre.ssw.proxy.compare.profile.IERecord;
import com.sabre.ssw.proxy.compare.profile.Profile;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class CustomDiffIncludeExcludeLayout extends VerticalLayout
{

    private static final long serialVersionUID = 1L;
    private CheckBox checkBox;
    private CustomDiffWindow customDiffWindow;
    private Table includeTable;
    private Table excludeTable;
    private CustomDiffEditIERecordWindow editIEWindow;

    public CheckBox getCheckBox()
    {
        return checkBox;
    }

    public CustomDiffIncludeExcludeLayout(CustomDiffWindow customDiffWindow)
    {
        this.customDiffWindow = customDiffWindow;
        this.editIEWindow = new CustomDiffEditIERecordWindow(customDiffWindow);
        initialize();
    }

    private void initialize()
    {
        VerticalLayout verticalIncludeTableLayout = createIncludeTabLayout();
        this.addComponent(verticalIncludeTableLayout);
        VerticalLayout verticalExcludeTableLayout = createExcludeTabLayout();
        this.addComponent(verticalExcludeTableLayout);
        this.addComponent(createIncludeOptionsCheckbox());

        this.setExpandRatio(verticalIncludeTableLayout, 1.0f);
        this.setExpandRatio(verticalExcludeTableLayout, 1.0f);
        this.setSizeFull();
        this.setSpacing(true);
    }

    private VerticalLayout createExcludeTabLayout()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        excludeTable = createExcludeTable();
        Label l = new Label("Excludes");
        l.setStyleName("border-label");
        verticalLayout.addComponent(l);
        verticalLayout.addComponent(excludeTable);
        verticalLayout.addComponent(createNewExcludeButton());
        verticalLayout.setExpandRatio(excludeTable, 1.0f);
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private VerticalLayout createIncludeTabLayout()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        includeTable = createIncludeTable();
        Label l = new Label("Includes");
        l.setStyleName("border-label");
        verticalLayout.addComponent(l);
        verticalLayout.addComponent(includeTable);
        verticalLayout.addComponent(createNewIncludeButton());
        verticalLayout.setExpandRatio(includeTable, 1.0f);
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private CheckBox createIncludeOptionsCheckbox()
    {
        checkBox = new CheckBox("Enable content file checking");
        checkBox.setImmediate(true);

        return checkBox;
    }

    private Table createIncludeTable()
    {
        Table table = new Table();
        table.setSizeFull();
        table.addContainerProperty("FILE", String.class, null);
        table.addContainerProperty("REGULAR EXPRESSION", Boolean.class, null);
        table.addContainerProperty("DELETE", Button.class, null);
        table.setImmediate(true);
        table.setSelectable(true);
        table.addListener(new ItemClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event)
            {
                if (event.isDoubleClick())
                {
                    try
                    {
                        customDiffWindow.getParent().addWindow(editIEWindow);
                        editIEWindow.show((IERecord) event.getItemId(), true, customDiffWindow.getContext(), customDiffWindow.getActiveProfile());
                    }
                    catch (Exception e)
                    {

                    }

                }
            }
        });
        return table;
    }

    private Table createExcludeTable()
    {
        Table table = new Table();
        table.setSizeFull();
        table.addContainerProperty("FILE", String.class, null);
        table.addContainerProperty("REGULAR EXPRESSION", Boolean.class, null);
        table.addContainerProperty("DELETE", Button.class, null);
        table.setImmediate(true);
        table.setSelectable(true);
        table.addListener(new ItemClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event)
            {
                if (event.isDoubleClick())
                {
                    try
                    {
                        customDiffWindow.getParent().addWindow(editIEWindow);
                        editIEWindow.show((IERecord) event.getItemId(), false, customDiffWindow.getContext(), customDiffWindow.getActiveProfile());
                    }
                    catch (Exception e)
                    {

                    }

                }
            }
        });
        return table;
    }

    private Button createNewIncludeButton()
    {
        Button newInclude = new Button("Add include");
        newInclude.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                addNewInclude();
            }
        });
        return newInclude;
    }

    private Button createNewExcludeButton()
    {
        Button newExclude = new Button("Add exclude");
        newExclude.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                addNewExclude();
            }
        });
        return newExclude;
    }

    private void addNewInclude()
    {
        try
        {
            customDiffWindow.getParent().addWindow(editIEWindow);
            IERecord record = new IERecord("*", false, false);
            editIEWindow.show(record, true, customDiffWindow.getContext(), customDiffWindow.getActiveProfile());
        }
        catch (Exception e)
        {
        }
    }

    private void addNewExclude()
    {
        try
        {
            customDiffWindow.getParent().addWindow(editIEWindow);
            IERecord record = new IERecord("*", false, false);
            editIEWindow.show(record, false, customDiffWindow.getContext(), customDiffWindow.getActiveProfile());
        }
        catch (Exception e)
        {
        }
    }

    public void refreshIncludeTable()
    {
        Profile p = customDiffWindow.getActiveProfile();
        includeTable.removeAllItems();

        if (p == null)
        {
            return;
        }
        for (final IERecord record : p.getIncludeList())
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
                    customDiffWindow.removeIncludeFromProfile(record);
                    refreshIncludeTable();
                }

            });
            Object[] objects = new Object[] { record.getFile(), record.isRegex(), b };
            includeTable.addItem(objects, record);
        }
    }

    public void refreshExcludeTable()
    {
        Profile p = customDiffWindow.getActiveProfile();
        excludeTable.removeAllItems();

        if (p == null)
        {
            return;
        }

        for (final IERecord record : p.getExcludeList())
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
                    customDiffWindow.removeExcludeFromProfile(record);
                    refreshExcludeTable();
                }
            });
            Object[] objects = new Object[] { record.getFile(), record.isRegex(), b };
            excludeTable.addItem(objects, record);
        }
    }

}
