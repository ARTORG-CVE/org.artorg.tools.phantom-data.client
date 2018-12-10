package org.artorg.tools.phantomData.client.tablesFilter.base;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.columns.AbstractColumn;
import org.artorg.tools.phantomData.client.table.columns.FilterColumn;
import org.artorg.tools.phantomData.client.tables.IPersonifiedColumns;
import org.artorg.tools.phantomData.server.model.base.Note;

public class NoteFilterTable extends DbUndoRedoFactoryEditFilterTable<Note> implements IPersonifiedColumns {

	{
		setTableName("Notes");

		setColumnCreator(items -> {
			List<AbstractColumn<Note,?>> columns =
				new ArrayList<AbstractColumn<Note,?>>();
			columns.add(new FilterColumn<Note,String>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			createPersonifiedColumns(columns);			
			return columns;
		});

	}

}