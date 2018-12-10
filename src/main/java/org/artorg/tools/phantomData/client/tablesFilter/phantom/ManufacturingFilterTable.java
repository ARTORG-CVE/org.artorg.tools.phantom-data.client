package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.columns.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.columns.IPropertyColumns;
import org.artorg.tools.phantomData.client.table.DbFilterTable;
import org.artorg.tools.phantomData.server.model.phantom.Manufacturing;

public class ManufacturingFilterTable 
extends DbFilterTable<Manufacturing>
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