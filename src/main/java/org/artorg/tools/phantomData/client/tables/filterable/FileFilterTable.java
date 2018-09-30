package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileFilterTable extends DbUndoRedoEditFilterTable<PhantomFile> {

	{
		setItemClass(PhantomFile.class);
		
		List<IColumn<PhantomFile>> columns =
				new ArrayList<IColumn<PhantomFile>>();
		columns.add(new Column<PhantomFile, PhantomFile>(
				"path", item -> item, 
				path -> path.getPath(), 
				(path,value) -> path.setPath(value)));
		columns.add(new Column<PhantomFile, PhantomFile>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		columns.add(new Column<PhantomFile, PhantomFile>(
				"extension", item -> item, 
				path -> path.getExtension(), 
				(path,value) -> path.setExtension(value)));
		columns.add(new Column<PhantomFile, FileType>(
				"file type", item -> item.getFileType(), 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		this.setColumns(columns);
		
		this.setTableName("Files");
	}

}
