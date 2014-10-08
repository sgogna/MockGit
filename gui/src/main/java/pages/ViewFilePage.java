package pages;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import helpers.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pages.components.PagesMenuPanel;

import formatters.XmlFormatter;
import org.vaadin.codemirror2.CodeMirror;
import org.vaadin.codemirror2.client.ui.CodeMode;
import pages.subwindows.ConfirmationWindow;

public class ViewFilePage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    protected static final String NAME = "preview";
    private PagesMenuPanel menuPanel = new PagesMenuPanel(this);
    private VerticalLayout vl = new VerticalLayout();
    private CodeMirror textarea = new CodeMirror(null);
    private ComboBox syntaxComboBox = new ComboBox(null);
    private Button xmlFormatterButton;
    private File file;
    private ConfirmationWindow confirmationWindow = new ConfirmationWindow();

    public ViewFilePage() {
        Button saveFileButton = new Button("Save File");
        saveFileButton.addListener(new saveFileListener());
        menuPanel.addComponent(saveFileButton);
        
        syntaxComboBox.setNullSelectionAllowed(false);
        syntaxComboBox.setTextInputAllowed(false);
        syntaxComboBox.setInvalidAllowed(false);
        syntaxComboBox.setImmediate(true);
        
        for (CodeMode codeMode : CodeMode.values()) {
            syntaxComboBox.addItem(codeMode);
        }
        syntaxComboBox.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                CodeMode codeMode = (CodeMode)event.getProperty().getValue();
                if(codeMode != null){
                    textarea.setCodeMode(codeMode);
                }
            }

        });
        syntaxComboBox.select(CodeMode.XML);
        
//        textarea.setWidth("489");
//        textarea.setHeight("500px");
        textarea.setShowLineNumbers(true);
        xmlFormatterButton = new Button("XML Format");
        xmlFormatterButton.addListener(new Button.ClickListener(){

            @Override
            public void buttonClick(Button.ClickEvent event) {
                textarea.setValue(XmlFormatter.format((String)textarea.getValue()));
            }
        
        });
        
        
        setName(NAME);
        this.addComponent(menuPanel);
        Panel textPanel = new Panel();
        textPanel.setScrollable(true);
        textPanel.addComponent(vl);
        this.addComponent(textPanel);
    }

    public void openFile(File filepath) {
        this.file = filepath;
        vl.removeAllComponents();

        textarea.setCaption(filepath.getAbsolutePath());
        textarea.setValue(FileReader.readFile(filepath));
        
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(syntaxComboBox);
        hl.addComponent(xmlFormatterButton);
        vl.addComponent(hl);
        vl.addComponent(textarea);
    }

    class saveFileListener implements Button.ClickListener {

        private static final long serialVersionUID = 1L;

        public void buttonClick(Button.ClickEvent event) {
            initSaveFileConfirmationWindow();
        }
    }

    private void saveFile(File dir, AbstractTextField area) {
        FileWriter out;
        if (dir != null) {
            try {
                out = new FileWriter(dir);
                out.write(area.toString());
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initSaveFileConfirmationWindow() {
        confirmationWindow.setInformationText("Are you sure you want to save this file?");
        confirmationWindow.setOKButtonCaption("Yes");
        confirmationWindow.setCancelButtonCaption("No");
        addWindow(confirmationWindow);
        confirmationWindow.addOkButtonListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveFile(file, textarea);
                openFile(file);
            }
        });
        confirmationWindow.addCancelButtonListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                openFile(file);
            }
        });
    }
}
