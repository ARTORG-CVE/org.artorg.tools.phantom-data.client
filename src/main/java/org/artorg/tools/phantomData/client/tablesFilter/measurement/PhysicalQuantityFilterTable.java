package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.measurement.PhysicalQuantity;

public class PhysicalQuantityFilterTable extends DbUndoRedoFactoryEditFilterTable<PhysicalQuantity> implements IPropertyColumns, IPersonifiedColumns {

	
	{
		setTableName("Unit Prefixes");

		setColumnCreator(items -> {
			List<AbstractColumn<PhysicalQuantity>> columns =
				new ArrayList<AbstractColumn<PhysicalQuantity>>();
			columns.add(new FilterColumn<PhysicalQuantity>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			columns.add(new FilterColumn<PhysicalQuantity>(
				"Symbol", item -> item,
				path -> path.getCommonSymbols(),
				(path, value) -> path.setCommonSymbols(value)));
			columns.add(new FilterColumn<PhysicalQuantity>(
				"Description", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
			createBaseColumns(columns);
			return columns;
		});

	}

}