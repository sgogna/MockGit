/* Copyright 2009 EB2 International Limited */
package pages.components.concurrentrequests;

import com.vaadin.data.Property.ValueChangeListener;

public class ConcurrentRequestChangeFlag
{
    private boolean isChange = false;
    private ValueChangeListener changeListener;

    public boolean isChange()
    {
        return isChange;
    }

    public void setChange(boolean isChange)
    {
        this.isChange = isChange;
        if (changeListener != null) {
            changeListener.valueChange(null);
        }
    }
    
    public void setChangeListener(ValueChangeListener changeListener)
    {
        this.changeListener = changeListener;
    }
}
