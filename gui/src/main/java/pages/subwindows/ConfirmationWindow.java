/* Copyright 2009 EB2 International Limited */
package pages.subwindows;

import com.vaadin.ui.Label;

public class ConfirmationWindow extends BaseWindow
{
    private static final long serialVersionUID = 1L;

    private Label infoText = new Label();

    public ConfirmationWindow()
    {
        super("Confirmation");
        setMainComponent(infoText);
    }

    public void setInformationText(String txt)
    {
        infoText.setCaption(txt);
    }

}
