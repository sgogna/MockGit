/* Copyright 2009 EB2 International Limited */
package pages.subwindows;

import java.util.List;

import spring.SpringBeanContainer;

import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.ComboBox;

public class ChooseDestinationDirForCopyWindow extends BaseWindow
{
    private static final long serialVersionUID = 1L;

    private ComboBox dirs = new ComboBox("Dirs list:");

    public ChooseDestinationDirForCopyWindow()
    {
        super("Select a destination dir");
        dirs.setNullSelectionAllowed(false);
        dirs.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        dirs.setWidth(300, UNITS_PIXELS);
        setMainComponent(dirs);

    }

    public void show()
    {
        dirs.removeAllItems();
        List<String> airlines = SpringBeanContainer.getMainFileManager().getAirlines();
        for (String s : airlines)
        {
            dirs.addItem(s);
        }
    }

    public String getSelectedFolder()
    {
        return (String) dirs.getValue();
    }
}
