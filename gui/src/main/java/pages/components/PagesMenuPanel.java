package pages.components;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class PagesMenuPanel extends Panel
{
    private static final long serialVersionUID = 1L;
    HorizontalLayout leftLayout = new HorizontalLayout();
    HorizontalLayout rightLayout = new HorizontalLayout();
    private GridLayout layout = new GridLayout(2, 1);
    private Window w;

    public PagesMenuPanel(Window w)
    {
        this.w = w;
        Button backToMain = new Button("Home Page");
        backToMain.addListener(new BackToMainListener());

        layout.setSizeFull();
        layout.addComponent(leftLayout);
        layout.addComponent(rightLayout);
        layout.setComponentAlignment(leftLayout, new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER));
        leftLayout.addComponent(backToMain);
        leftLayout.setSpacing(true);
        leftLayout.setMargin(true);
        layout.setMargin(false);

        super.setContent(layout);
    }

    public void addBackButton(final ExternalResource resource)
    {
        Button back = new Button("Back <<");
        back.addListener(new ClickListener()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
                w.open(resource);
            }
        });

        leftLayout.addComponent(back, 1);
    }

    class BackToMainListener implements ClickListener
    {
        private static final long serialVersionUID = 1L;

        public void buttonClick(ClickEvent event)
        {

            w.open(new ExternalResource(getApplication().getURL()));
        }
    }

    public void addComponent(Component component)
    {
        leftLayout.addComponent(component);
    }

    public void addComponent(Component component, Alignment alignment)
    {
        if (alignment.isRight())
        {
            rightLayout.addComponent(component);
            layout.setComponentAlignment(rightLayout, new Alignment(Bits.ALIGNMENT_RIGHT));
        }
        else
        {
            addComponent(component);
        }
    }

    public void addComponent(Component component, int place)
    {
        leftLayout.addComponent(component, place);
    }
}
