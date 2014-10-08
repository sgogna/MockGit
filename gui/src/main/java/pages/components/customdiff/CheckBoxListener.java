/* Copyright 2009 EB2 International Limited */
package pages.components.customdiff;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

class CheckBoxListener implements ClickListener
{
    private static final long serialVersionUID = 1L;
    ComboBox comboBox = null;

    public CheckBoxListener(ComboBox comboBox)
    {
        this.comboBox = comboBox;
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
        event.getButton().booleanValue();
        comboBox.setValue("*");
    }
}