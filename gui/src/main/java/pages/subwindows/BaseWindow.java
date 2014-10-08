/* Copyright 2009 EB2 International Limited */
package pages.subwindows;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class BaseWindow extends Window
{
    private static final long serialVersionUID = 1L;
    private Button okButton = new Button("OK");
    private Button cancelButton = new Button("Cancel");
    private GridLayout layout = new GridLayout(1, 2);
    private HorizontalLayout buttonLayout = new HorizontalLayout();
    private VerticalLayout informationLayout = new VerticalLayout();
    private Button.ClickListener closeOKListener;
    private Button.ClickListener closeCancelListener;
    private Boolean okCloseEnabled = true;

    public BaseWindow(String name)
    {
        super(name);
        setModal(true);
        setResizable(false);
        closeOKListener = new ClickListener()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                if (okCloseEnabled) {
                close();
                }
            }
        };
        
        closeCancelListener = new ClickListener()
        {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void buttonClick(ClickEvent event)
            {
                    close();
            }
        };
    }

    public void setMainComponent(Component component)
    {
        initializeWindow(component);
    }

    private void initializeWindow(Component component)
    {
        

        okButton.addListener(closeOKListener);
        cancelButton.addListener(closeCancelListener);
        okButton.setWidth(75, UNITS_PIXELS);
        cancelButton.setWidth(75, UNITS_PIXELS);

        this.setWidth(400, Sizeable.UNITS_PIXELS);
        this.setHeight(100, Sizeable.UNITS_PIXELS);
        this.center();

        informationLayout.addComponent(component);
        informationLayout.setComponentAlignment(component, new Alignment(Bits.ALIGNMENT_HORIZONTAL_CENTER));
        informationLayout.setSpacing(true);

        buttonLayout.addComponent(okButton);
        buttonLayout.addComponent(cancelButton);
        buttonLayout.setSpacing(true);

        layout.addComponent(informationLayout);
        layout.setComponentAlignment(informationLayout, new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER));
        layout.addComponent(buttonLayout);
        layout.setComponentAlignment(buttonLayout, new Alignment(Bits.ALIGNMENT_HORIZONTAL_CENTER));
        layout.setSizeFull();

        setContent(layout);
    }

    public void setOKButtonCaption(String caption)
    {
        okButton.setCaption(caption);
    }

    public void setCancelButtonCaption(String caption)
    {
        cancelButton.setCaption(caption);
    }

    public void addOkButtonListener(ClickListener listener)
    {
        okButton.addListener(listener);
    }

    public void addCancelButtonListener(ClickListener listener)
    {
        cancelButton.addListener(listener);
    }
    
    public void close() {
        super.close();
    }
    
    public Boolean getOkCloseEnabled()
    {
        return okCloseEnabled;
    }
    
    public void setOkCloseEnabled(Boolean okCloseEnabled)
    {
        this.okCloseEnabled = okCloseEnabled;
    }
    
    public void addNewButton(Button button) {
        button.setWidth(75, UNITS_PIXELS);
        button.addListener(closeCancelListener);
        buttonLayout.addComponent(button);
    }
    
    public void removeButton(Button button) {
        buttonLayout.removeComponent(button);
    }
}
