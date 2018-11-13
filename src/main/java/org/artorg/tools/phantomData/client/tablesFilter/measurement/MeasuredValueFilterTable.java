package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.measurement.MeasuredValue;

public class MeasuredValueFilterTable extends DbUndoRedoFactoryEditFilterTable<MeasuredValue> implements IPropertyColumns, IPersonifiedColumns {

	
	{
		setTableName("Measured Values");

		setColumnCreator(items -> {
			List<AbstractColumn<MeasuredValue,?>> columns =
				new ArrayList<AbstractColumn<MeasuredValue,?>>();
			columns.add(new FilterColumn<MeasuredValue,String>(
				"Unit", item -> item.getUnit(),
				path -> path.getShortcut(),
				(path, value) -> path.setShortcut(value)));
			columns.add(new FilterColumn<MeasuredValue,String>(
				"Value", item -> item,
				path -> path.getValue().toString(),
				(path, value) -> path.setValue(Double.valueOf(value))));
			columns.add(new FilterColumn<MeasuredValue,String>(
				"Description", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
			createPersonifiedColumns(columns);
			return columns;
		});

	}

}