package pages.components;

import com.vaadin.terminal.ThemeResource;

public interface Icons {
	String newFileIcon = "icons/new_file.gif";
	String errorFileIcon = "icons/error.gif";
	String yellowIcon = "icons/yellow.PNG";
	
	ThemeResource newFile = new ThemeResource(newFileIcon);
	ThemeResource errorFile = new ThemeResource(errorFileIcon);
		
}
