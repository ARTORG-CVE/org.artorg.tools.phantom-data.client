package org.artorg.tools.phantomData.client.tablesFilter.base.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.client.table.IPersonifiedColumns;
import org.artorg.tools.phantomData.server.model.base.property.PropertyField;

public class PropertyFieldFilterTable
	extends DbUndoRedoFactoryEditFilterTable<PropertyField> implements IPersonifiedColumns {

	{
		setTableName("Property Field");

		setColumnCreator(items -> {
			List<AbstractColumn<PropertyField,?>> columns =
				new ArrayList<AbstractColumn<PropertyField,?>>();
			columns.add(new FilterColumn<PropertyField,String>(
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
			columns.add(new FilterColumn<PropertyField,String>(
				"Name", item -> item,
				path -> path.getName(),
				(path, value) -> path.setName((String) value)));
			columns.add(new FilterColumn<PropertyField,String>(
				"Description", item -> item,
				path -> path.getDescription(),
				(path, value) -> path.setDescription((String) value)));
			createPersonifiedColumns(columns);
			
			return columns;
		});

	}

}
