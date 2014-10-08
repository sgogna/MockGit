package pages.subwindows;

import com.vaadin.ui.TextField;

public class InputWindow extends BaseWindow {

    private static final long serialVersionUID = 1L;
    private TextField textField = new TextField();

    public InputWindow(String title) {
        super(title);
        setMainComponent(textField);
    }

    public String getInputText() {
        return (String) textField.getValue();
    }
    
}
