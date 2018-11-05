package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.table.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.measurement.Measurement;

@SuppressWarnings("deprecation")
public class MeasurementFilterTable extends DbUndoRedoFactoryEditFilterTable<Measurement> implements IPropertyColumns, IPersonifiedColumns {

	
	{
		setTableName("Measurements");

		setColumnCreator(items -> {
			List<AbstractColumn<Measurement>> columns =
				new ArrayList<AbstractColumn<Measurement>>();
			columns.add(new FilterColumn<Measurement>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			columns.add(new FilterColumn<Measurement>(
				"Date", item -> item,
				path -> new SimpleDateFormat(path.getDateFormat()).format(path.getStartDate()),
				(path, value) -> path.setStartDate(new Date(value))));
			columns.add(new FilterColumn<Measurement>(
				"Description", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
			createBaseColumns(columns);
			return columns;
		});

	}

}