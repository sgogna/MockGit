/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pages.AbstractPage;
import pages.subwindows.ConfirmationWindow;

import com.sabre.ssw.proxy.compare.profile.IERecord;
import com.sabre.ssw.proxy.compare.profile.Profile;
import com.sabre.ssw.proxy.compare.profile.TagRecord;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import filehandlers.ProfileManager;

public class CustomDiffWindow extends Window
{
    private static final long serialVersionUID = 1L;
    private DiffTypeComboBox profileComboBox;
    private Profile activeProfile = null;
    private ProfileManager profileManager;
    private Button deleteProfileButton;
    private CheckBox skipNamespace;
    private TabSheet tabSheet;
    private CheckBox contentEqualsCheckBox;
    private File context;

    private List<Profile> profileList = new ArrayList<Profile>();
    private ConfirmationWindow confirmationWindow = new ConfirmationWindow();
    private Button saveButton;
    private Set<String> changesList = new HashSet<String>();
    protected boolean profileChanging = false;
    private boolean wasDeleted = false;
    private CustomDiffIncludeExcludeLayout includeExcludeLayout;
    private CustomDiffTableTagsLayout tableTagsLayout;

    public Profile getActiveProfile()
    {
        return activeProfile;
    }

    public File getContext()
    {
        return context;
    }

    public CustomDiffWindow()
    {
        super("Custom diff editor");
        tableTagsLayout = new CustomDiffTableTagsLayout(this);
        includeExcludeLayout = new CustomDiffIncludeExcludeLayout(this);
    }

    public ProfileManager getProfileManager()
    {
        return profileManager;
    }

    public void setProfileManager(ProfileManager profileManager)
    {
        this.profileManager = profileManager;

    }

    @SuppressWarnings("unused")
    private void initializeWindow()
    {

        this.setWidth(70, Sizeable.UNITS_PERCENTAGE);
        this.setHeight(500, Sizeable.UNITS_PIXELS);
        this.center();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        verticalLayout.addComponent(createProfilePanel());
        tabSheet = createTabSheet();
        verticalLayout.addComponent(tabSheet);
        verticalLayout.addComponent(createActionsPanel());

        verticalLayout.setExpandRatio(tabSheet, 1.0f);
        this.setContent(verticalLayout);

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

    }

    protected void closeWindow()
    {
        if (wasDeleted)
        {
            Window parent = this.getParent();
            if (parent != null)
            {
                ((AbstractPage) parent).refresh();
            }
        }
        super.close();
    }

    private TabSheet createTabSheet()
    {
        TabSheet tabSheet = new TabSheet();
        VerticalLayout tableTagsLayout = new VerticalLayout();
        tableTagsLayout.addComponent(createTabletagsPanel());
        tableTagsLayout.setSizeFull();

        VerticalLayout includeLayout = new VerticalLayout();
        includeLayout.addComponent(createTableIncludePanel());
        includeLayout.setSizeFull();

        tabSheet.addTab(includeLayout, "Include/Exclude files");
        tabSheet.addTab(tableTagsLayout, "Rule editor");
        tabSheet.getTab(tableTagsLayout).setEnabled(false);
        tabSheet.setSizeFull();
        tabSheet.setEnabled(false);

        return tabSheet;
    }

    private VerticalLayout createTableIncludePanel()
    {
        contentEqualsCheckBox = includeExcludeLayout.getCheckBox();
        contentEqualsCheckBox.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                activeProfile.setContentCompare((Boolean) event.getProperty().getValue());
                if ((Boolean) event.getProperty().getValue())
                {
                    tabSheet.getTab(1).setEnabled(true);
                }
                else
                {
                    tabSheet.getTab(1).setEnabled(false);
                }
                if (!profileChanging)
                {
                    updateChangesList(contentEqualsCheckBox.getCaption(), ((Boolean) event.getProperty().getValue()) ? 1 : 0, activeProfile);
                }
            }
        });
        return includeExcludeLayout;
    }

    private VerticalLayout createTabletagsPanel()
    {
        skipNamespace = tableTagsLayout.getSkipNamespace();
        skipNamespace.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                changeSkipNamespace((Boolean) skipNamespace.getValue());
                if (!profileChanging)
                {
                    updateChangesList("skipNameSpace", ((Boolean) skipNamespace.getValue()) == true ? 1 : 0, activeProfile);
                }
            }
        });
        return tableTagsLayout;
    }

    protected void changeSkipNamespace(Boolean value)
    {
        activeProfile.setSkipNameSpace(value);
    }

    private Panel createActionsPanel()
    {
        Panel actionsPanel = new Panel();
        actionsPanel.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
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

        Button cancel = new Button("Close");
        cancel.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                close();
            }
        });

        saveButton.setWidth(100, Sizeable.UNITS_PIXELS);
        cancel.setWidth(100, Sizeable.UNITS_PIXELS);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.addComponent(saveButton);
        horizontalLayout.setSizeFull();
        horizontalLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
        horizontalLayout.setComponentAlignment(cancel, Alignment.BOTTOM_RIGHT);
        horizontalLayout.setExpandRatio(cancel, 1.0f);
        horizontalLayout.setSpacing(true);
        actionsPanel.setContent(horizontalLayout);
        return actionsPanel;
    }

    protected void save()
    {
        if (!profileManager.saveAll(profileList))
        {
            getWindow().showNotification("ERROR", "Error during saving data.", Notification.TYPE_ERROR_MESSAGE);

        }

        Window parent = this.getParent();
        if (parent != null)
        {
            ((AbstractPage) parent).refresh();
        }
        closeWindow();
    }

    public boolean removeIncludeFromProfile(IERecord record)
    {
        Profile profile = activeProfile;
        if (profile == null)
            return false;
        updateChangesList("saveIERecord" + record, 0, activeProfile);
        return profile.removeInclude(record);
    }

    public boolean removeExcludeFromProfile(IERecord record)
    {
        Profile profile = activeProfile;
        if (profile == null)
            return false;
        updateChangesList("saveIERecord" + record, 0, activeProfile);
        return profile.removeExclude(record);
    }

    protected void removeProfile(final String profileName)
    {
        ConfirmationWindow deleteProfileConfirmationWindow = new ConfirmationWindow();
        deleteProfileConfirmationWindow.setOKButtonCaption("Yes");
        deleteProfileConfirmationWindow.setCancelButtonCaption("No");
        deleteProfileConfirmationWindow.setInformationText("Are you sure to delete this profile?");
        deleteProfileConfirmationWindow.addOkButtonListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                if (profileManager.removeProfile(profileName))
                {
                    wasDeleted = true;
                    removeallProfileUpdate(activeProfile);
                    profileList.remove(activeProfile);
                    activeProfile = null;
                    refreshProfilePanel();
                }
                else
                {
                    CustomDiffWindow.this.getWindow().showNotification("ERROR", "Error during deleting profile.", Notification.TYPE_ERROR_MESSAGE);
                }

            }
        });
        getWindow().getParent().addWindow(deleteProfileConfirmationWindow);

    }

    public boolean removeTagFromProfile(TagRecord record)
    {
        Profile profile = activeProfile;
        if (profile == null)
            return false;
        updateChangesList("SaveTagRecord" + record, 0, activeProfile);
        return profile.removeTag(record);
    }

    private Panel createProfilePanel()
    {
        Panel profilePanel = new Panel();
        profilePanel.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        profileComboBox = new DiffTypeComboBox();
        profileComboBox.setNullSelectionAllowed(false);
        profileComboBox.setInputPrompt("Please select a profile");
        profileComboBox.setStyleName("profileComboBox");
        profileComboBox.setImmediate(true);
        profileComboBox.addListener(new Property.ValueChangeListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event)
            {
                if (profileComboBox.isListenerEnabled())
                {
                    changeProfile((Profile) event.getProperty().getValue());
                }

            }
        });
        Button createProfileButton = new Button("Create new profile");
        createProfileButton.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                createNewProfile();
            }
        });

        deleteProfileButton = new Button("Delete profile");
        deleteProfileButton.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {

                removeProfile(activeProfile.getName());
            }
        });
        deleteProfileButton.setEnabled(false);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.addComponent(profileComboBox);
        horizontalLayout.addComponent(createProfileButton);
        horizontalLayout.addComponent(deleteProfileButton);
        horizontalLayout.setComponentAlignment(createProfileButton, Alignment.MIDDLE_LEFT);
        profilePanel.setContent(horizontalLayout);
        return profilePanel;

    }

    protected void createNewProfile()
    {
        final Window newProfileWindow = new Window("New Profile");
        newProfileWindow.setModal(true);
        newProfileWindow.setResizable(false);
        newProfileWindow.setDraggable(false);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setSizeUndefined();

        final TextField textField = new TextField("Enter profile name");
        textField.setImmediate(true);
        Button button = new Button("OK");
        button.addListener(new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                if (addNewProfile((String) textField.getValue()))
                {
                    CustomDiffWindow.this.getParent().removeWindow(newProfileWindow);
                }
                else
                {
                    textField.setComponentError(new UserError("Incorrect name (null or existing)!"));
                }
            }
        });
        verticalLayout.addComponent(textField);
        verticalLayout.addComponent(button);
        verticalLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
        newProfileWindow.setContent(verticalLayout);
        this.getParent().addWindow(newProfileWindow);
    }

    protected boolean addNewProfile(String value)
    {
        if (value.isEmpty())
            return false;
        for (Profile p : profileList)
        {
            if (p.getName().equals(value))
            {
                return false;
            }
        }
        Profile newProfile = new Profile(value);
        profileList.add(newProfile);
        activeProfile = newProfile;
        updateChangesList("Profile", 1, activeProfile);
        changeProfile(newProfile);
        refreshProfilePanel();
        return true;
    }

    private void refreshProfilePanel()
    {
        profileChanging = true;
        profileComboBox.setListenerEnabled(false);
        profileComboBox.removeAllItems();
        for (Profile p : profileList)
        {
            profileComboBox.addItem(p);
        }
        profileComboBox.setListenerEnabled(true);
        if (activeProfile != null)
        {
            profileComboBox.setValue(activeProfile);
        }
        else
        {
            changeProfile(null);
        }
        profileChanging = false;
    }

    private void changeProfile(Profile profile)
    {
        profileChanging = true;
        activeProfile = profile;
        if (activeProfile == null)
        {
            tabSheet.setEnabled(false);
            deleteProfileButton.setEnabled(false);
        }
        else
        {
            tabSheet.setEnabled(true);
            deleteProfileButton.setEnabled(true);

        }

        refreshInEx();
        tableTagsLayout.refreshTableTags();
        includeExcludeLayout.refreshExcludeTable();
        includeExcludeLayout.refreshIncludeTable();
        refreshProfileOptions();
        tabSheet.setSelectedTab(tabSheet.getTab(0).getComponent());
        profileChanging = false;
    }

    private void refreshInEx()
    {
        Profile p = activeProfile;
        if (p == null)
        {
            return;
        }
        contentEqualsCheckBox.setValue(p.isContentCompare());
    }

    private void refreshProfileOptions()
    {
        if (activeProfile != null)
        {
            skipNamespace.setValue(activeProfile.isSkipNameSpace());
        }
    }

    public void saveTagRecord(TagRecord record)
    {
        Profile p = activeProfile;
        if (p != null)
        {
            if (!p.getTagList().contains(record))
            {
                p.addTag(record);
                updateChangesList("SaveTagRecord" + record, 1, activeProfile);
            }
            else
            {
                updateChangesList("SaveTagRecord" + record, 2, activeProfile);
            }
        }
        tableTagsLayout.refreshTableTags();
    }

    public void setContext(File f)
    {
        this.context = f;
    }

    public void refresh()
    {
        String name = "null";
        if (profileManager.getActiveProfile() != null)
        {
            name = profileManager.getActiveProfile().getName();
        }
        activeProfile = null;
        for (Profile p : profileList)
        {
            if (p.getName().equals(name))
            {
                activeProfile = p;
                break;
            }
        }
        refreshProfilePanel();
    }

    @Override
    protected void close()
    {
        if (changesList.size() > 0)
        {
            this.getParent().addWindow(confirmationWindow);
        }
        else
        {
            closeWindow();
        }
    }

    public void saveIERecord(IERecord record, boolean includeFlag)
    {
        Profile p = activeProfile;
        if (p != null)
        {
            if (includeFlag)
            {
                if (!p.getIncludeList().contains(record))
                {
                    p.addInclude(record);
                    updateChangesList("saveIERecord" + record, 1, activeProfile);
                }
                else
                {
                    updateChangesList("saveIERecord" + record, 2, activeProfile);
                }
                includeExcludeLayout.refreshIncludeTable();
            }
            else
            {
                if (!p.getExcludeList().contains(record))
                {
                    p.addExclude(record);
                    updateChangesList("saveIERecord" + record, 1, activeProfile);
                }
                else
                {
                    updateChangesList("saveIERecord" + record, 2, activeProfile);
                }
                includeExcludeLayout.refreshExcludeTable();

            }
        }
    }

    public void show(File file, Window window)
    {
        wasDeleted = false;
        if (this.getParent() != null)
        {
            this.getParent().removeWindow(this);
        }
        window.addWindow(this);
        changesList.clear();
        checkChanges();
        profileList = profileManager.getCopyOfProfileList();
        setContext(file);
        refresh();
    }

    private void checkChanges()
    {
        if (changesList.size() > 0)
        {
            saveButton.setEnabled(true);
        }
        else
        {
            saveButton.setEnabled(false);
        }
    }

    private void updateChangesList(Object object, int status, Profile profile)
    {
        int opposite = status == 1 ? 0 : (status == 0 ? 1 : status);
        if (isOppositeAction(object, opposite, profile))
        {
            changesList.remove(convertChangesToString(object, opposite, profile));
        }
        else
        {
            changesList.add(convertChangesToString(object, status, profile));
        }
        checkChanges();
    }

    private boolean isOppositeAction(Object object, int status, Profile profile)
    {
        return changesList.contains(convertChangesToString(object, status, profile));
    }

    private String convertChangesToString(Object object, int status, Profile profile)
    {
        return profile.getName() + "|" + object.toString() + "|" + status;
    }

    private void removeallProfileUpdate(Profile activeProfile2)
    {
        Iterator<String> iterator = changesList.iterator();
        while (iterator.hasNext())
        {
            String text = iterator.next();
            if (text.startsWith(activeProfile2.getName()))
            {
                iterator.remove();
            }
        }
        checkChanges();
    }

}
