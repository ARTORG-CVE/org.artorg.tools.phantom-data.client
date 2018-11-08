package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.server.model.phantom.LiteratureBase;

public class LiteratureBaseFilterTable
	extends DbUndoRedoFactoryEditFilterTable<LiteratureBase> implements IPersonifiedColumns {

	{
		setTableName("Literature Bases");

		setColumnCreator(items -> {
			List<AbstractColumn<LiteratureBase,?>> columns =
				new ArrayList<AbstractColumn<LiteratureBase,?>>();
			columns.add(new FilterColumn<LiteratureBase,String>(
				"Shortcut", item -> item,
				path -> path.getShortcut(),
				(path, value) -> path.setShortcut((String) value)));
			columns.add(new FilterColumn<LiteratureBase,String>(
				"Value", item -> item,
				path -> path.getValue(),
				(path, value) -> path.setValue((String) value)));
			createBaseColumns(columns);
			return columns;
		});

	}

}
