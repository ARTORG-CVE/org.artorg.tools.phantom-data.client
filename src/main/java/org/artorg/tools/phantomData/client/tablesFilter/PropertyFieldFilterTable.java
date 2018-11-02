package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IBaseColumns;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldFilterTable
	extends DbUndoRedoFactoryEditFilterTable<PropertyField> implements IBaseColumns {

	{
		setTableName("Property Field");

		setColumnCreator(items -> {
			List<AbstractColumn<PropertyField>> columns =
				new ArrayList<AbstractColumn<PropertyField>>();
			columns.add(new FilterColumn<PropertyField>(
				"Type", item -> item,
				path -> {
					try {
						return Class.forName(path.getType()).getSimpleName();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return path.getType();
				},
				(path, value) -> {}));
			columns.add(new FilterColumn<PropertyField>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName((String) value)));
			columns.add(new FilterColumn<PropertyField>(
				"Description", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
			createBaseColumns(columns);
			
			return columns;
		});

	}

}
