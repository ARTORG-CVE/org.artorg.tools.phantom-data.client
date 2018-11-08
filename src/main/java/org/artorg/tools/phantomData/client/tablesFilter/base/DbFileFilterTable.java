package org.artorg.tools.phantomData.client.tablesFilter.base;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileSystemView;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.server.model.base.DbFile;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class DbFileFilterTable extends DbUndoRedoFactoryEditFilterTable<DbFile>
	implements IPersonifiedColumns {

	{
		setTableName("Files");
		
		setColumnCreator(items -> {
			List<AbstractColumn<DbFile,?>> columns =
				new ArrayList<AbstractColumn<DbFile,?>>();
			columns.add(new FilterColumn<DbFile,Image>("", item -> item,
				path -> getFileIcon(path.getFile().getPath()), (path, value) -> {}));
			
			columns.add(new FilterColumn<DbFile,String>("Name", item -> item,
				path -> path.getName(), (path, value) -> path.setName(value)));
			columns.add(new FilterColumn<DbFile,String>("Extension", item -> item,
				path -> path.getExtension(), (path, value) -> path.setExtension(value)));
			columns.add(new FilterColumn<DbFile,String>("File Tags", item -> item,
				path -> path.getFileTags().stream().map(fileTag -> fileTag.getName()).collect(Collectors.joining(", ")), 
				(path, value) -> {}));
			createBaseColumns(columns);
			return columns;
		});

	}
	
	
	
	static HashMap<String, Image> mapOfFileExtToSmallIcon = new HashMap<String, Image>();
	
	private static Image getFileIcon(String fname) {
        final String ext = getFileExt(fname);

        Image fileIcon = mapOfFileExtToSmallIcon.get(ext);
        if (fileIcon == null) {

            javax.swing.Icon jswingIcon = null; 

            File file = new File(fname);
            if (file.exists()) {
                jswingIcon = getJSwingIconFromFileSystem(file);
            }
            else {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("icon", ext);
                    jswingIcon = getJSwingIconFromFileSystem(tempFile);
                }
                catch (IOException e) {
                	e.printStackTrace();
                }
                finally {
                    if (tempFile != null) tempFile.delete();
                }
            }

            if (jswingIcon != null) {
                fileIcon = jswingIconToImage(jswingIcon);
                mapOfFileExtToSmallIcon.put(ext, fileIcon);
            }
        }

        return fileIcon;
    }
	
	private static javax.swing.Icon getJSwingIconFromFileSystem(File file) {

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
	
	private static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
	
	private static String getFileExt(String fname) {
        String ext = ".";
        int p = fname.lastIndexOf('.');
        if (p >= 0) {
            ext = fname.substring(p);
        }
        return ext.toLowerCase();
    }

}
