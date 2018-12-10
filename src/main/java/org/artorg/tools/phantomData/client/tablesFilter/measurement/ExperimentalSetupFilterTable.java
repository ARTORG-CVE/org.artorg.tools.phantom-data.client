package org.artorg.tools.phantomData.client.tablesFilter.measurement;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.columns.IPersonifiedColumns;
import org.artorg.tools.phantomData.client.columns.IPropertyColumns;
import org.artorg.tools.phantomData.client.table.DbTable;
import org.artorg.tools.phantomData.server.model.measurement.ExperimentalSetup;

public class ExperimentalSetupFilterTable
	extends DbTable<ExperimentalSetup>
	implements IPropertyColumns, IPersonifiedColumns {

	{
		setTableName("Exeperimental Setups");

		setColumnCreator(items -> {
			List<AbstractColumn<ExperimentalSetup, ?>> columns =
				new ArrayList<AbstractColumn<ExperimentalSetup, ?>>();
			columns.add(new FilterColumn<ExperimentalSetup, String>("Short name",
				item -> item, path -> path.getShortName(),
				(path, value) -> path.setShortName(value)));
			columns.add(new FilterColumn<ExperimentalSetup, String>("Long name",
				item -> item, path -> path.getLongName(),
				(path, value) -> path.setLongName(value)));
			columns.add(new FilterColumn<ExperimentalSetup, String>("Description",
				item -> item, path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
			columns.add(new FilterColumn<ExperimentalSetup, String>("Files", item -> item,
				path -> String.valueOf(path.getFiles().size()), (path, value) -> {}));
			columns.add(new FilterColumn<ExperimentalSetup, String>("Notes", item -> item,
				path -> String.valueOf(path.getNotes().size()), (path, value) -> {}));

			createPersonifiedColumns(columns);
			return columns;
		});
	}
}