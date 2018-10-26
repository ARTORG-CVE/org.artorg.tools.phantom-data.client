package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.DbFile;

public class FileFilterTable extends DbUndoRedoFactoryEditFilterTable<DbFile> {

	{
		setTableName("Files");

		setColumnCreator(items -> {
			List<AbstractColumn<DbFile>> columns =
				new ArrayList<AbstractColumn<DbFile>>();
			columns.add(new FilterColumn<DbFile>(
				"name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			columns.add(new FilterColumn<DbFile>(
				"extension", item -> item,
				path -> path.getExtension(),
				(path, value) -> path.setExtension(value)));
			columns.add(new FilterColumn<DbFile>(
				"file type", item -> item.getFileType(),
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			return columns;
		});

	}

}
