package org.artorg.tools.phantomData.client.tablesFilter.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.server.model.base.DbFile;

public class DbFileFilterTable extends DbUndoRedoFactoryEditFilterTable<DbFile>
	implements IPersonifiedColumns {

	{
		setTableName("Files");
		
		setColumnCreator(items -> {
			List<AbstractColumn<DbFile>> columns =
				new ArrayList<AbstractColumn<DbFile>>();
			columns.add(new FilterColumn<DbFile>("Name", item -> item,
				path -> path.getName(), (path, value) -> path.setName(value)));
			columns.add(new FilterColumn<DbFile>("Extension", item -> item,
				path -> path.getExtension(), (path, value) -> path.setExtension(value)));
			columns.add(new FilterColumn<DbFile>("File Tags", item -> item,
				path -> path.getFileTags().stream().map(fileTag -> fileTag.getName()).collect(Collectors.joining(", ")), 
				(path, value) -> {}));
			createBaseColumns(columns);
			return columns;
		});

	}

}
