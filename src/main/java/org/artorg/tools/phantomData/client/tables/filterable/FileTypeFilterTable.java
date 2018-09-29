package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeFilterTable extends DbUndoRedoEditFilterTable<FileType> {

	{
		setItemClass(FileType.class);
		
		List<IColumn<FileType>> columns =
				new ArrayList<IColumn<FileType>>();
		columns.add(new Column<FileType, FileType>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		this.setColumns(columns);
		
		this.setTableName("File Types");
	}
	
}
