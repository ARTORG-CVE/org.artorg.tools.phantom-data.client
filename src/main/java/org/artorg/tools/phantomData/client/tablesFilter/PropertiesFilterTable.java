package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IBaseColumns;
import org.artorg.tools.phantomData.server.model.property.Properties;

public class PropertiesFilterTable
	extends DbUndoRedoFactoryEditFilterTable<Properties> implements IBaseColumns {

	{
		setTableName("Properties");

		setColumnCreator(items -> {
			List<AbstractColumn<Properties>> columns =
				new ArrayList<AbstractColumn<Properties>>();

			columns.add(new FilterColumn<Properties>(
				"type", item -> item.getBooleanProperties().get(0).getPropertyField(),
				path -> {
					try {
						return Class.forName(path.getType()).getSimpleName();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return path.getType();
				},
				(path, value) -> {}));
			columns.add(new FilterColumn<Properties>(
				"name", item -> item.getBooleanProperties().get(0).getPropertyField(),
				path -> path.getName(),
				(path, value) -> path.setName(value)));
			columns.add(new FilterColumn<Properties>(
				"value", item -> item.getBooleanProperties().get(0),
				path -> path.toString(path.getValue()),
				(path, value) -> path.setValue(path.fromStringToValue(value))));
			columns.add(new FilterColumn<Properties>(
				"description",
				item -> item.getBooleanProperties().get(0).getPropertyField(),
				path -> path.getDescription(),
				(path, value) -> path.setDescription(value)));
			createBaseColumns(columns);
			return columns;
		});
	}

}
