package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldFilterTable extends DbUndoRedoEditFilterTable<PropertyField> {
	
	{
		setItemClass(PropertyField.class);
		
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
		this.setColumns(columns);
		
		this.setTableName("Property Field");
	}

}
