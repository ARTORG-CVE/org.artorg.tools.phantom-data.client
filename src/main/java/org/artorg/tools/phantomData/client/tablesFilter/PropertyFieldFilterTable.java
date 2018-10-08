package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldFilterTable extends DbUndoRedoFactoryEditFilterTable<PropertyField> {
	
	{
		List<AbstractColumn<PropertyField>> columns =
				new ArrayList<AbstractColumn<PropertyField>>();
		columns.add(new FilterColumn<PropertyField>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		columns.add(new FilterColumn<PropertyField>(
				"description", item -> item, 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription((String) value)));
		columns.add(new FilterColumn<PropertyField>(
				"entity", item -> item, 
				path -> path.getParentItemClass().getSimpleName(), 
				(path,value) -> {
					try {
						path.setParentItemClass(Class.forName(value));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					throw new RuntimeException();
				}));
		
		this.setColumns(columns);
		
		this.setTableName("Property Field");
	}

}
