package listeners;

import pages.components.FilesDiffTable;

import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

public class SaveFileListener implements Listener {
	private static final long serialVersionUID = 1L;
    private FilesDiffTable textAreaHandler;

	public SaveFileListener(FilesDiffTable textAreaHandler) {

		this.textAreaHandler = textAreaHandler;
		
	}

	public void componentEvent(Event event) {
		textAreaHandler.saveFiles();

	}

}
