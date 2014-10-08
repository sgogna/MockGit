package pages.components;

import helpers.TableRowList;
import pages.AbstractPage;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class NavigationPanel extends Panel
{
    private static final long serialVersionUID = 1L;
    private Button nextButton = new Button(">>");
    private Button prevButton = new Button("<<");
    private Label listPosition = new Label();
    private HorizontalLayout layout = new HorizontalLayout();

    private TableRowList rowList = null;
    private AbstractPage page = null;

    public NavigationPanel()
    {
        prepareButtonsListener();

        layout.setSpacing(true);
        layout.addComponent(prevButton);
        layout.addComponent(listPosition);
        layout.addComponent(nextButton);
        this.setSizeUndefined();

        super.addComponent(layout);
    }

    public void setTableInfo(TableRowList list, AbstractPage page)
    {
        this.rowList = list;
        this.page = page;
        setLabelValue();
        enableButtons();
    }

    private void enableButtons()
    {
        if (isFirstElement())
            prevButton.setEnabled(false);
        else
            prevButton.setEnabled(true);

        if (isLastElement())
            nextButton.setEnabled(false);
        else
            nextButton.setEnabled(true);
    }

    private boolean isLastElement()
    {
        if (rowList.getCurrentRowIndex() == rowList.getSize() - 1)
            return true;

        return false;
    }

    private boolean isFirstElement()
    {
        if (rowList.getCurrentRowIndex() == 0)
            return true;

        return false;
    }

    public TableRowList getRowList()
    {
        return rowList;
    }

    public void setLabelValue()
    {
        int currentRow = rowList.getCurrentRowIndex() + 1;

        listPosition.setValue(currentRow + " / " + rowList.getSize());
    }

    private void prepareButtonsListener()
    {
        nextButton.addListener(new ClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                rowList.getNext();
                page.refreshAfterClick();
                setLabelValue();
            }

        });

        prevButton.addListener(new ClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                rowList.getPrevious();
                page.refreshAfterClick();
                setLabelValue();
            }
        });
    }
}
