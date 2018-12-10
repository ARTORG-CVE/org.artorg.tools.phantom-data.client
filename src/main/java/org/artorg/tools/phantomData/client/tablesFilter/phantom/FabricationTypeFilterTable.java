package org.artorg.tools.phantomData.client.tablesFilter.phantom;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.columns.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.server.model.phantom.FabricationType;

public class FabricationTypeFilterTable
	extends DbTable<FabricationType> implements IPersonifiedColumns {

	{
		setTableName("Fabrication Types");

		setColumnCreator(items -> {
			List<AbstractColumn<FabricationType,?>> columns =
				new ArrayList<AbstractColumn<FabricationType,?>>();
			columns.add(new FilterColumn<FabricationType,String>(
				"Shortcut", item -> item,
				path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
			columns.add(new FilterColumn<FabricationType,String>(
				"Value", item -> item,
				path -> path.getValue(),
				(path, value) -> path.setValue(value)));
			createPersonifiedColumns(columns);
			return columns;
		});

	}

}
