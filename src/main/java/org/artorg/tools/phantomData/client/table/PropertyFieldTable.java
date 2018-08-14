package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.Column;
import org.artorg.tools.phantomData.client.specification.Column2;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyFieldTable extends StageTable<PropertyFieldTable,PropertyField, Integer> {
	
	@Override
	public HttpDatabaseCrud<PropertyField, Integer> getConnector() {
		return PropertyFieldConnector.get();
	}

	@Override
	public List<Column<PropertyField, ? extends DatabasePersistent<?, ?>, ?, ?>> createColumns2() {
		List<Column<PropertyField, ? extends DatabasePersistent<?, ?>, ?, ?>> columns =
				new ArrayList<Column<PropertyField, ? extends DatabasePersistent<?, ?>, ?, ?>>();
		columns.add(new Column<PropertyField, PropertyField, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId(value)));
		columns.add(new Column<PropertyField, PropertyField, String, Integer>(
				"name", item -> item, 
				path -> path.getName(), 
				(path,value) -> path.setName(value)));
		columns.add(new Column<PropertyField, PropertyField, String, Integer>(
				"description", item -> item, 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription(value)));
		return columns;
	}

}
