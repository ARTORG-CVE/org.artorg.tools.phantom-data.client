package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeFilterTable extends DbUndoRedoFactoryEditFilterTable<FileType> {

	{
		List<AbstractColumn<FileType>> columns =
				new ArrayList<AbstractColumn<FileType>>();
		columns.add(new FilterColumn<FileType>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		this.setColumns(columns);
		
		this.setTableName("File Types");
	}
	
}
