package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeFilterTable extends DbUndoRedoEditFilterTable<FileType> {

	{
		setItemClass(FileType.class);
		
		List<Column<FileType>> columns =
				new ArrayList<Column<FileType>>();
		columns.add(new LambdaColumn<FileType, FileType>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		this.setColumns(columns);
		
		this.setTableName("File Types");
	}
	
}
