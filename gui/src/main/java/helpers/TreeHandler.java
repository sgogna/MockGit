package helpers;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pages.components.Icons;
import proxy.compare.CompareResult;
import proxy.compare.FolderComparator;

import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Tree;

public class TreeHandler implements Icons{
private Tree tree;
private FolderComparator folderComparator = new FolderComparator();

public TreeHandler(Tree tree) {
	this.tree = tree;

}
	
public void collapseAll(FilesystemContainer fsc) {
	Collection<File> items = fsc.getItemIds();
	for (File i : items)
		tree.collapseItem(i);
}
public void addIcons(List<File> files, ThemeResource icon) {

	for (File i : files) {
		String tmp = i.getPath();
		while (tree.containsId(new File(tmp))) {

			tree.setItemIcon(new File(tmp), icon);
			int t = tmp.lastIndexOf("\\");
			if (t > 0)
				tmp = tmp.substring(0, t);

		}
	}
}
public void compareAll(String recDir) {

	List<CompareResult> compareResults = folderComparator
			.compareAllFolders(recDir);
	for (CompareResult j : compareResults) {

		addIcons(j.getNewFilesInFirstDir(), newFile);
		addIcons(j.getNewFilesInSecondDir(), newFile);
		addIcons(j.getErrorFilesInFirstDir(), errorFile);
		addIcons(j.getErrorFilesInSecondDir(), errorFile);

	}
}
public Iterator<File> compareDirs() {
	if (!checkIfTwoFolders())
		return null;
    Set<?> set = (Set<?>) tree.getValue();
	Iterator<?> tmp = set.iterator();

	return (Iterator<File>) tmp;

}

public void clearIcons(FilesystemContainer fsc) {
	for (File i : fsc.getItemIds())
		tree.setItemIcon(i, null);
}

public boolean checkIfTwoFolders() {
	Set< ? > set = (Set< ? >) tree.getValue();

	if (set.size() == 2) {

		Iterator< ? > tmp = set.iterator();
		String first = tmp.next().toString();
		if (!isDir(first))
			return false;
		first = tmp.next().toString();
		if (!isDir(first))
			return false;
		return true;
	}
	return false;
}

private Boolean isDir(String path) {
	File tmp = new File(path);
	if (tmp.isDirectory())
		return true;
	else
		return false;
}
}
