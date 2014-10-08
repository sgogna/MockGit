/* Copyright 2009 EB2 International Limited */
package pages.subwindows;

import java.io.File;
import java.util.Set;

import com.vaadin.ui.ListSelect;

public class MainPageDeleteTreeItems extends BaseWindow
{

    private static final long serialVersionUID = 1L;
    private ListSelect listSelect = new ListSelect("Are you shure to delete this items? "); 
    
    public MainPageDeleteTreeItems()
    {
        super("Confirmation");
        listSelect.setReadOnly(true);
        listSelect.setWidth(300, UNITS_PIXELS);
        listSelect.setHeight(100, UNITS_PIXELS);
        listSelect.setNullSelectionAllowed(false);
        setMainComponent(listSelect);
        setHeight(200, UNITS_PIXELS);
    }
    
    public void show(Set<?> list)
    {
        listSelect.removeAllItems();
        for (Object o:list) {
            listSelect.addItem(((File)o).getName());
        }
    }

}
