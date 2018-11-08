package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.measurement.UnitPrefix;

public class UnitPrefixFilterTable extends DbUndoRedoFactoryEditFilterTable<UnitPrefix> implements IPropertyColumns, IPersonifiedColumns {

	
	{
		setTableName("Unit Prefixes");

		setColumnCreator(items -> {
			List<AbstractColumn<UnitPrefix,?>> columns =
				new ArrayList<AbstractColumn<UnitPrefix,?>>();
			columns.add(new FilterColumn<UnitPrefix,String>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			columns.add(new FilterColumn<UnitPrefix,String>(
				"Prefix", item -> item,
				path -> path.getPrefix(),
				(path, value) -> path.setPrefix(value)));
			columns.add(new FilterColumn<UnitPrefix,String>(
				"Exponent", item -> item,
				path -> path.getExponent().toString(),
				(path, value) -> path.setExponent(Integer.valueOf(value))));
			createBaseColumns(columns);
			return columns;
		});

	}

}