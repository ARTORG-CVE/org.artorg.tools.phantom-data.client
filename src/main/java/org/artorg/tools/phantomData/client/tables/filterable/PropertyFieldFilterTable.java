package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.client.table.LambdaColumn;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldFilterTable extends DbUndoRedoEditFilterTable<PropertyField> {
	
	{
		setItemClass(PropertyField.class);
		
		List<Column<PropertyField>> columns =
				new ArrayList<Column<PropertyField>>();
		columns.add(new LambdaColumn<PropertyField, PropertyField>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		columns.add(new LambdaColumn<PropertyField, PropertyField>(
				"description", item -> item, 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription((String) value)));
		this.setColumns(columns);
		
		this.setTableName("Property Field");
	}

}
