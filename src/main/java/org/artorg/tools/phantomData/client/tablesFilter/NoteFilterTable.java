package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IBaseColumns;
import org.artorg.tools.phantomData.server.model.Note;

public class NoteFilterTable extends DbUndoRedoFactoryEditFilterTable<Note> implements IBaseColumns {

	{
		setTableName("Notes");

		setColumnCreator(items -> {
			List<AbstractColumn<Note>> columns =
				new ArrayList<AbstractColumn<Note>>();
			columns.add(new FilterColumn<Note>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			createBaseColumns(columns);			
			return columns;
		});

	}

}