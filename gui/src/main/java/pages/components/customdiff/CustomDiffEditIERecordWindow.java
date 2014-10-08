/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import java.io.File;

import com.sabre.ssw.proxy.compare.profile.IERecord;
import com.sabre.ssw.proxy.compare.profile.Profile;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CustomDiffEditIERecordWindow extends Window
{
    private static final long serialVersionUID = 1L;
    private CustomDiffWindow customDiffWindow;
    private Button saveButton;
    private MyListener listener = new MyListener();
    private CustomDiffFilesComboBox customDiffFilesComboBox;

    public CustomDiffEditIERecordWindow(CustomDiffWindow customDiffWindow)
    {
        this.setModal(true);
        this.setResizable(false);
        this.customDiffWindow = customDiffWindow;
        this.customDiffFilesComboBox = new CustomDiffFilesComboBox();
        initializeComponents();
    }

    private void initializeComponents()
    {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setSizeUndefined();
        verticalLayout.setWidth(600, Sizeable.UNITS_PIXELS);

        verticalLayout.addComponent(customDiffFilesComboBox.getFileVerticalLayout());

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
                CustomDiffEditIERecordWindow.this.getParent().removeWindow(CustomDiffEditIERecordWindow.this);
            }
        });
        saveButton = new Button("Add");
        saveButton.addListener(listener);
        cancel.setWidth(80, UNITS_PIXELS);
        saveButton.setWidth(80, UNITS_PIXELS);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.addComponent(saveButton);
        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);
        this.setContent(verticalLayout);
    }

    public void show(final IERecord record, boolean include, File context, Profile profile)
    {
        customDiffFilesComboBox.show(record.getFile(), record.isRegex(), record.isCommand(), context, profile);
        listener.setRecord(record);
        listener.setIncludeFlag(include);
    }

    private class MyListener implements ClickListener
    {
        private static final long serialVersionUID = 1L;
        private IERecord record;
        private boolean includeFlag;

        public void setRecord(IERecord record)
        {
            this.record = record;
        }

        public void setIncludeFlag(boolean includeFlag)
        {
            this.includeFlag = includeFlag;
        }

        @Override
        public void buttonClick(ClickEvent event)
        {
            record.setFile((String) customDiffFilesComboBox.getFileComboBox().getValue());
            record.setRegex((Boolean) customDiffFilesComboBox.getFileRegexCheckBox().getValue());
            record.setCommand(customDiffFilesComboBox.isCommandChecked());

            customDiffWindow.saveIERecord(record, includeFlag);
            customDiffWindow.getParent().removeWindow(CustomDiffEditIERecordWindow.this);
        }
    }

}
