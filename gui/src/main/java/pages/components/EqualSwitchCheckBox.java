package pages.components;

import listeners.EqualSwitchListener;

import com.vaadin.ui.CheckBox;

public class EqualSwitchCheckBox extends CheckBox
{

    private static final long serialVersionUID = 1L;

    public EqualSwitchCheckBox(Boolean initialState)
    {
        super("show equal comparisons", initialState);
        setImmediate(true);
        addListener(new EqualSwitchListener());
    }

}
