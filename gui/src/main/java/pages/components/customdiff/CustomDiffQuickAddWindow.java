/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pages.AbstractPage;
import pages.subwindows.ConfirmationWindow;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.sabre.ssw.proxy.compare.profile.TagRecord;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import filehandlers.ProfileManager;

public class CustomDiffQuickAddWindow extends Window
{
    private static final long serialVersionUID = 1L;
    private ProfileManager profileManager;
    private List<TagRecord> tagRecordList = new ArrayList<TagRecord>();
    private int pagesCounter = 0;
    private int lastIndex = 0;
    private HorizontalLayout navigationLayout;
    private Label countLabel;
    private String newFileName;
    private String originalFileName;
    private Button prev;
    private Button next;
    private ConfirmationWindow confirmationWindow = new ConfirmationWindow();
    private Button saveButton;
    private Button removeAct;
    private CustomDiffEditTagRecordWindow editTagPanel;
    private File context;

    public CustomDiffQuickAddWindow()
    {
        this.center();
        this.setResizable(false);
        this.setCaption("Add skipped tag/attribute");
    }

    public void initialize()
    {
        confirmationWindow.setInformationText("Are you sure to close without saving?");
        confirmationWindow.setOKButtonCaption("Yes");
        confirmationWindow.setCancelButtonCaption("No");
        confirmationWindow.addOkButtonListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                closeWindow();
            }
        });

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setSizeUndefined();
        verticalLayout.setWidth(600, Sizeable.UNITS_PIXELS);

        Button addNew = new Button("Add new");
        addNew.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                addNew();
            }
        });

        removeAct = new Button("Remove");
        removeAct.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                removeAct();
            }
        });

        prev = new Button("<");
        prev.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                lastIndex = pagesCounter;
                pagesCounter--;
                show();
            }
        });
        next = new Button(">");
        next.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                lastIndex = pagesCounter;
                pagesCounter++;
                show();
            }
        });
        countLabel = new Label();

        navigationLayout = new HorizontalLayout();
        navigationLayout.setMargin(false);
        navigationLayout.setSpacing(false);
        navigationLayout.addComponent(prev);
        navigationLayout.addComponent(countLabel);
        navigationLayout.addComponent(next);

        HorizontalLayout horizontalLayoutTop = new HorizontalLayout();
        horizontalLayoutTop.setSizeFull();
        horizontalLayoutTop.setMargin(false);
        horizontalLayoutTop.setSpacing(false);

        horizontalLayoutTop.addComponent(addNew);
        horizontalLayoutTop.addComponent(removeAct);
        horizontalLayoutTop.setExpandRatio(removeAct, 1.0f);
        horizontalLayoutTop.addComponent(navigationLayout);
        horizontalLayoutTop.setComponentAlignment(navigationLayout, Alignment.MIDDLE_RIGHT);

        verticalLayout.addComponent(horizontalLayoutTop);

        editTagPanel = new CustomDiffEditTagRecordWindow(null, false);
        VerticalLayout layout = editTagPanel.getMainLayout();
        layout.setSizeFull();
        layout.setMargin(false);
        verticalLayout.addComponent(layout);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(false);
        horizontalLayout.setMargin(false);
        horizontalLayout.setSizeFull();
        Button cancel = new Button("Cancel");
        cancel.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                close();
            }
        });
        saveButton = new Button("Save");
        saveButton.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                save();
            }
        });
        cancel.setWidth(80, UNITS_PIXELS);
        saveButton.setWidth(80, UNITS_PIXELS);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
        horizontalLayout.addComponent(saveButton);
        horizontalLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);

        this.setContent(verticalLayout);
    }

    protected void removeAct()
    {
        tagRecordList.remove(pagesCounter - 1);
        if (pagesCounter > 1)
        {
            pagesCounter--;
        }
        lastIndex = pagesCounter;
        show();
    }

    private void changeFileName()
    {
        String end = originalFileName.substring(originalFileName.length() - 6, originalFileName.length());
        newFileName = originalFileName.replaceFirst("_\\d*?_" + end, "_*_" + end);
    }

    protected boolean isNotEmptyOnList()
    {
        for (TagRecord tagRecord : tagRecordList)
        {
            if (!tagRecord.getFile().equals("*") || !tagRecord.getTag().equals("*") || !tagRecord.getAttribute().equals("*"))
            {
                return true;
            }
        }
        return false;
    }

    protected void save()
    {
        lastIndex = pagesCounter;
        saveCurrentView();
        Profile p = profileManager.getActiveProfile();
        if (p != null)
        {
            List<Profile> list = profileManager.getProfileList();
            for (Profile prof : list)
            {
                if (prof.getName().equals(p.getName()))
                {
                    for (TagRecord tagRecord : tagRecordList)
                    {
                        if (!tagRecord.getTag().equals("*") || !tagRecord.getFile().equals("*") || !tagRecord.getAttribute().equals("*"))
                        {
                            prof.addTag(tagRecord);
                        }
                    }
                    break;
                }
            }
            profileManager.saveAll(list);
            ((AbstractPage) getParent()).refresh();
        }
        super.close();
    }

    public ProfileManager getProfileManager()
    {
        return profileManager;
    }

    public void setProfileManager(ProfileManager profileManager)
    {
        this.profileManager = profileManager;
    }

    public void show(File file)
    {
        pagesCounter = 0;
        lastIndex = pagesCounter;
        tagRecordList.clear();
        originalFileName = file.getName();

        changeFileName();
        context = file;
        addNew();
    }

    private void addNew()
    {
        lastIndex = pagesCounter;
        pagesCounter = tagRecordList.size() + 1;
        if (lastIndex < 1)
        {
            lastIndex = pagesCounter;
        }
        TagRecord tagRecord = new TagRecord(newFileName, false, "*", "*", false, false, false);
        tagRecordList.add(tagRecord);
        show();
    }

    private void show()
    {
        countLabel.setValue(pagesCounter + "/" + tagRecordList.size());
        if (pagesCounter == 1)
        {
            prev.setEnabled(false);
        }
        else
        {
            prev.setEnabled(true);
        }

        if (pagesCounter == tagRecordList.size())
        {
            next.setEnabled(false);
        }
        else
        {
            next.setEnabled(true);
        }

        if (tagRecordList.size() > 1)
        {
            removeAct.setEnabled(true);
        }
        else
        {
            removeAct.setEnabled(false);
        }
        if (lastIndex != pagesCounter)
        {
            saveCurrentView();
        }
        TagRecord tagRecord = tagRecordList.get(pagesCounter - 1);
        boolean isSkipNameSpace = profileManager.getActiveProfile() == null ? false : profileManager.getActiveProfile().isSkipNameSpace();
        editTagPanel.show(tagRecord, "", isSkipNameSpace, context.getParentFile(), profileManager.getActiveProfile());
    }

    private void saveCurrentView()
    {

        TagRecord lastTagRecord = tagRecordList.get(lastIndex - 1);
        lastTagRecord.setFile((String) editTagPanel.getFileComboBox().getValue());
        lastTagRecord.setFileCheckBoxValue(editTagPanel.getFileRegexCheckBox().booleanValue());
        lastTagRecord.setTag((String) editTagPanel.getTagComboBox().getValue());
        lastTagRecord.setTagCheckBoxValue(editTagPanel.getTagRegexCheckBox().booleanValue());
        lastTagRecord.setAttribute((String) editTagPanel.getAtrComboBox().getValue());
        lastTagRecord.setAttributeCheckBoxValue(editTagPanel.getAtrRegexCheckBox().booleanValue());

    }

    @Override
    protected void close()
    {
        if (saveButton.isEnabled())
        {
            this.getParent().addWindow(confirmationWindow);
        }
        else
        {
            closeWindow();
        }
    }

    private void closeWindow()
    {
        super.close();
    }

}
