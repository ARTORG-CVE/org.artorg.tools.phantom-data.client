package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.ColumnOptional;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyFieldTable extends StageTable<PropertyFieldTable,PropertyField, Integer> {
	
	{
		this.setConnector(PropertyFieldConnector.get());
	}

	@Override
	public List<IColumn<PropertyField, ?, ?>> createColumns() {
		List<IColumn<PropertyField, ?, ?>> columns =
				new ArrayList<IColumn<PropertyField, ?, ?>>();
		columns.add(new Column<PropertyField, PropertyField, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId((Integer) value)));
		columns.add(new Column<PropertyField, PropertyField, String, Integer>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		columns.add(new Column<PropertyField, PropertyField, String, Integer>(
				"description", item -> item, 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription((String) value)));
		return columns;
	}

}
