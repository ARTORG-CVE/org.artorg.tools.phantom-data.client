package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.columns.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.columns.IPropertyColumns;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.server.model.phantom.Special;

public class SpecialFilterTable extends DbUndoRedoFactoryEditFilterTable<Special>
		implements IPropertyColumns, IPersonifiedColumns {

	{
		setTableName("Specials");

		setColumnCreator(items -> {
			List<AbstractColumn<Special,?>> columns = new ArrayList<AbstractColumn<Special,?>>();
			columns.add(new FilterColumn<Special,String>("Shortcut", item -> item, path -> path.getShortcut(),
					(path, value) -> path.setShortcut(value)));
			columns.add(new FilterColumn<Special,String>("Description", item -> item, path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
//			createPropertyColumns(columns, this.getItems());
			createPersonifiedColumns(columns);
			return columns;
		});

	}

}
