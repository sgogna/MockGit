/* Copyright 2009 EB2 International Limited */
package pages.components;

import java.io.File;

import com.vaadin.data.util.FilesystemContainer;

import fileManagers.MainFileManager;

public class GuiFileSystemContainer extends FilesystemContainer
{
    private static final long serialVersionUID = 1L;

    public GuiFileSystemContainer(File root)
    {
        super(root);
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException
    {
        return MainFileManager.deleteTest((File) itemId);
    }
 
    
    
    
}
