package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldFilterTable extends FilterTableSpringDb<PropertyField, Integer> {
	
	{
		this.setConnector(PropertyFieldConnector.get());
	}

	@Override
	public List<IColumn<PropertyField, ?>> createColumns() {
		List<IColumn<PropertyField, ?>> columns =
				new ArrayList<IColumn<PropertyField, ?>>();
		columns.add(new Column<PropertyField, PropertyField, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				PropertyFieldConnector.get()));
		columns.add(new Column<PropertyField, PropertyField, Integer>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value),
				PropertyFieldConnector.get()));
		columns.add(new Column<PropertyField, PropertyField, Integer>(
				"description", item -> item, 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription((String) value),
				PropertyFieldConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Property Fields";
	}

}
