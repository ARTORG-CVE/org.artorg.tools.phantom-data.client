package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.columns.AbstractColumn;
import org.artorg.tools.phantomData.client.table.columns.FilterColumn;
import org.artorg.tools.phantomData.client.tables.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.tables.IPropertyColumns;
import org.artorg.tools.phantomData.server.model.measurement.Measurement;

public class MeasurementFilterTable extends DbUndoRedoFactoryEditFilterTable<Measurement> implements IPropertyColumns, IPersonifiedColumns {
	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	
	{
		setTableName("Measurements");

		setColumnCreator(items -> {
			List<AbstractColumn<Measurement,?>> columns =
				new ArrayList<AbstractColumn<Measurement,?>>();
			columns.add(new FilterColumn<Measurement,String>(
				"Date", item -> item,
				path -> format.format(path.getStartDate()),
				(path, value) -> {
					try {
						path.setStartDate(format.parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}));
			columns.add(new FilterColumn<Measurement,String>(
				"Person", item -> item.getPerson(),
				path -> path.toName(),
				(path, value) -> {}
				));
			columns.add(new FilterColumn<Measurement,String>(
				"Project", item -> item.getProject(),
				path -> path.toName(),
				(path, value) -> {}
				));
			columns.add(new FilterColumn<Measurement,String>(
				"Experimental Setup", item -> item.getExperimentalSetup(),
				path -> path.getShortName(),
				(path, value) -> {}
				));
			columns.add(new FilterColumn<Measurement,String>(
				"Files", item -> item,
				path -> String.valueOf(path.getFiles().size()),
				(path, value) -> {}));
			columns.add(new FilterColumn<Measurement,String>(
				"Notes", item -> item,
				path -> String.valueOf(path.getNotes().size()),
				(path, value) -> {}));
			createPersonifiedColumns(columns);
			return columns;
		});

	}

}