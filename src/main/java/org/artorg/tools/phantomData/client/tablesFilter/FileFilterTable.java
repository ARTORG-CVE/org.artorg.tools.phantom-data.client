package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.PhantomFile;

public class FileFilterTable extends DbUndoRedoFactoryEditFilterTable<PhantomFile> {

	{
		List<AbstractColumn<PhantomFile>> columns =
				new ArrayList<AbstractColumn<PhantomFile>>();
		columns.add(new FilterColumn<PhantomFile>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		columns.add(new FilterColumn<PhantomFile>(
				"extension", item -> item, 
				path -> path.getExtension(), 
				(path,value) -> path.setExtension(value)));
		columns.add(new FilterColumn<PhantomFile>(
				"file type", item -> item.getFileType(), 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		this.setColumns(columns);
		
		this.setTableName("Files");
	}

}
