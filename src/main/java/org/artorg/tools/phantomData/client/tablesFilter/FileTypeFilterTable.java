package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IBaseColumns;
import org.artorg.tools.phantomData.server.model.FileType;

public class FileTypeFilterTable extends DbUndoRedoFactoryEditFilterTable<FileType> implements IBaseColumns {

	{
		setTableName("File Types");

		setColumnCreator(items -> {
			List<AbstractColumn<FileType>> columns =
				new ArrayList<AbstractColumn<FileType>>();
			columns.add(new FilterColumn<FileType>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName((String) value)));
			createBaseColumns(columns);
			return columns;
		});

	}

}
