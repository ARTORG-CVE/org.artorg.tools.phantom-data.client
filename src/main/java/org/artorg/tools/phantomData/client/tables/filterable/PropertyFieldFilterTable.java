package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.control.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldFilterTable extends FilterTableSpringDb<PropertyField> {
	
	{
		setItemClass(PropertyField.class);
	}

	@Override
	public List<IColumn<PropertyField>> createColumns() {
		List<IColumn<PropertyField>> columns =
				new ArrayList<IColumn<PropertyField>>();
		columns.add(new Column<PropertyField, PropertyField>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		columns.add(new Column<PropertyField, PropertyField>(
				"description", item -> item, 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription((String) value)));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Property Fields";
	}

}
