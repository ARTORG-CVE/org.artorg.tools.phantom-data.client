package org.artorg.tools.phantomData.client.tablesFilter.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.server.model.base.FileTag;

public class FileTagFilterTable extends DbUndoRedoFactoryEditFilterTable<FileTag> implements IPersonifiedColumns {
	
	{
		setTableName("File Tags");

		setColumnCreator(items -> {
			List<AbstractColumn<FileTag,?>> columns =
				new ArrayList<AbstractColumn<FileTag,?>>();
			columns.add(new FilterColumn<FileTag,String>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			createBaseColumns(columns);			
			return columns;
		});

	}

}
