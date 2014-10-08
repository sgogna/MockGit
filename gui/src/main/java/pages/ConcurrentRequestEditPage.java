/* Copyright 2009 EB2 International Limited */
package pages;

import pages.components.PagesMenuPanel;
import pages.components.concurrentrequests.ConcurrentRequestsRuleEditComponent;
import spring.SpringBeanContainer;

import com.vaadin.ui.VerticalLayout;

public class ConcurrentRequestEditPage extends AbstractPage
{

    private static final long serialVersionUID = 1L;
    private ConcurrentRequestsRuleEditComponent editComponent;
    public static final String NAME = "concurrent";
    
    public ConcurrentRequestEditPage()
    {
        setName(NAME);
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();
        layout.addComponent(new PagesMenuPanel(this));
        editComponent = new ConcurrentRequestsRuleEditComponent();
        editComponent.setRules(SpringBeanContainer.getConcurrentRequestsFilesManager().readRules());
        layout.addComponent(editComponent);
        layout.setExpandRatio(editComponent, 1.0f);
        setContent(layout);
    }

}
