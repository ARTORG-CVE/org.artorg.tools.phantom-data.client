package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldTable extends StageTable<PropertyFieldTable,PropertyField, Integer> {
	
	@Override
	public HttpDatabaseCrud<PropertyField, Integer> getConnector() {
		return PropertyFieldConnector.get();
	}

	@Override
	public List<String> createColumnNames() {
		return Arrays.asList("id", "name", "description");
	}

	@Override
	public List<PropertyUndoable<PropertyField, Integer, Object>> createProperties() {
		List<PropertyUndoable<PropertyField, Integer, Object>> properties = 
				new ArrayList<PropertyUndoable<PropertyField, Integer, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.setName((String) o), 
				i -> i.getName()));
		properties.add(createProperty(
				(i,o) -> i.setDescription((String) o), 
				i -> i.getDescription()));
		return properties;
	}

}
