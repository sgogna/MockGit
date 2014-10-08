package filters;

import spring.SpringBeanContainer;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

import filehandlers.ProfileManager;

public class EqualSwitcherFilter implements Filter
{

    private static final long serialVersionUID = 1L;
    private ProfileManager profileManager;
    private String propertyId;
    private String equalString;

    public EqualSwitcherFilter(String propertyId, String equalString)
    {
        this.propertyId = propertyId;
        this.equalString = equalString;
        profileManager = SpringBeanContainer.getProfileManagerBean();
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedOperationException
    {

        Property property = item.getItemProperty(propertyId);

        if (property == null || !property.getType().equals(String.class))
            return false;

        String value = (String) property.getValue();

        if (!profileManager.isEqualSwitch() && value != null
                && value.equals(equalString))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean appliesToProperty(Object propertyId)
    {
        return propertyId != null && this.propertyId.equals(propertyId);
    }

}
