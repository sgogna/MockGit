/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import com.vaadin.ui.ComboBox;

public class DiffTypeComboBox extends ComboBox
{
    private static final long serialVersionUID = 1L;
    private boolean listenerEnabled;

    public DiffTypeComboBox(String string)
    {
        super(string);
    }
    
    

    public DiffTypeComboBox()
    {
        super();
        
    }



    public boolean isListenerEnabled()
    {
        return listenerEnabled;
    }

    public void setListenerEnabled(boolean listenerEnabled)
    {
        this.listenerEnabled = listenerEnabled;
    }
}
