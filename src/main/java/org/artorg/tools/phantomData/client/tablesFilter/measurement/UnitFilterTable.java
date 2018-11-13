package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.measurement.Unit;

public class UnitFilterTable extends DbUndoRedoFactoryEditFilterTable<Unit> implements IPropertyColumns, IPersonifiedColumns {

	{
		setTableName("Units");

		setColumnCreator(items -> {
			List<AbstractColumn<Unit,?>> columns =
				new ArrayList<AbstractColumn<Unit,?>>();
			columns.add(new FilterColumn<Unit,String>(
				"Shortcut", item -> item,
				path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));	
			columns.add(new FilterColumn<Unit,String>(
				"Unit Prefix", item -> item.getUnitPrefix(),
				path -> path.getPrefix(),
				(path, value) -> path.setPrefix(value)));	
			columns.add(new FilterColumn<Unit,String>(
				"Physical Quantity", item -> item.getPhysicalQuantity(),
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			columns.add(new FilterColumn<Unit,String>(
				"Description", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
			createPersonifiedColumns(columns);
			return columns;
		});

	}

}