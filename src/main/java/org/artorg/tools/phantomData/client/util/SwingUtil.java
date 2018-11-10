package org.artorg.tools.phantomData.client.util;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

public class SwingUtil {
	
	public static Icon getJSwingIconFromFileSystem(File file) {
        // Windows {
        FileSystemView view = FileSystemView.getFileSystemView();
        javax.swing.Icon icon = view.getSystemIcon(file);
        // }

        // OS X {
        //final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        //javax.swing.Icon icon = fc.getUI().getFileView(fc).getIcon(file);
        // }

        return icon;
    }

}
