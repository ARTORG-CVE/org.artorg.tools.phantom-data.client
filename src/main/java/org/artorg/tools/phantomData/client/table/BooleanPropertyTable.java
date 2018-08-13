package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyTable extends StageTable<BooleanPropertyTable, BooleanProperty, Integer> {

	@Override
	public HttpDatabaseCrud<BooleanProperty, Integer> getConnector() {
		return BooleanPropertyConnector.get();
	}

	@Override
	public List<String> createColumnNames() {
		return Arrays.asList("id", "property field", "value");
	}

	@Override
	public List<PropertyUndoable<BooleanProperty, Integer, Object>> createProperties() {
		List<PropertyUndoable<BooleanProperty, Integer, Object>> properties = 
				new ArrayList<PropertyUndoable<BooleanProperty, Integer, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.getPropertyField().setDescription((String) o), 
				i -> i.getPropertyField().getDescription()));
		properties.add(createProperty(
				(i,o) -> i.setBool((Boolean) o), 
				i -> i.getBool()));
		return properties;
	}

}
