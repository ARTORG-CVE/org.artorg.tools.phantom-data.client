package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.ColumnOptional;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyFieldTable extends Table<PropertyFieldTable,PropertyField, Integer> {
	
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

}
