package org.artorg.tools.phantomData.client.tablesFilter.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.columns.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.server.model.base.FileTag;

public class FileTagFilterTable extends DbTable<FileTag> implements IPersonifiedColumns {
	
	{
		setTableName("File Tags");

		setColumnCreator(items -> {
			List<AbstractColumn<FileTag,?>> columns =
				new ArrayList<AbstractColumn<FileTag,?>>();
			columns.add(new FilterColumn<FileTag,String>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			createPersonifiedColumns(columns);			
			return columns;
		});

	}

}
