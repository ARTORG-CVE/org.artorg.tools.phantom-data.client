package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.phantom.Manufacturing;

public class ManufacturingFilterTable 
extends DbUndoRedoFactoryEditFilterTable<Manufacturing>
implements IPropertyColumns, IPersonifiedColumns {

{
	setTableName("Manufacturings");

	setColumnCreator(items -> {
		List<AbstractColumn<Manufacturing, ?>> columns =
			new ArrayList<AbstractColumn<Manufacturing, ?>>();
		columns.add(new FilterColumn<Manufacturing, String>("Name",
			item -> item, path -> path.getName(),
			(path, value) -> path.setName(value)));
		columns.add(new FilterColumn<Manufacturing, String>("Description",
			item -> item, path -> path.getDescription(),
			(path, value) -> path.setDescription(value)));
		columns.add(new FilterColumn<Manufacturing, String>("Files", item -> item,
			path -> String.valueOf(path.getFiles().size()), (path, value) -> {}));
		columns.add(new FilterColumn<Manufacturing, String>("Notes", item -> item,
			path -> String.valueOf(path.getNotes().size()), (path, value) -> {}));

		createPersonifiedColumns(columns);
		return columns;
	});
}
}