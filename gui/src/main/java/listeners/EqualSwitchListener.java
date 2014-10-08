package listeners;

import spring.SpringBeanContainer;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import filehandlers.ProfileManager;

public class EqualSwitchListener implements ClickListener
{

    private static final long serialVersionUID = 1L;
    private ProfileManager profileManager;

    public EqualSwitchListener()
    {
        profileManager = SpringBeanContainer.getProfileManagerBean();
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
        Boolean equalSwitch = event.getButton().booleanValue();
        profileManager.setEqualSwitch(equalSwitch);
    }

}
