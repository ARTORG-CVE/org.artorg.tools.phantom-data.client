package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.phantom.Special;

public class SpecialFilterTable extends DbUndoRedoFactoryEditFilterTable<Special>
		implements IPropertyColumns, IPersonifiedColumns {

	{
		setTableName("Specials");

		setColumnCreator(items -> {
			List<AbstractColumn<Special,?>> columns = new ArrayList<AbstractColumn<Special,?>>();
			columns.add(new FilterColumn<Special,String>("Shortcut", item -> item, path -> path.getShortcut(),
					(path, value) -> path.setShortcut(value)));
			createPropertyColumns(columns, this.getItems());
			createBaseColumns(columns);
			return columns;
		});

	}

}
