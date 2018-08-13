package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.FabricationType;

public class FabricationTypeTable extends StageTable<FabricationTypeTable, FabricationType, Integer> {

	@Override
	public HttpDatabaseCrud<FabricationType, Integer> getConnector() {
		return FabricationTypeConnector.get();
	}
	
	@Override
	public List<String> createColumnNames() {
		return Arrays.asList("id", "shortcut", "value");
	}

	@Override
	public List<PropertyUndoable<FabricationType, Integer, Object>> createProperties() {
		List<PropertyUndoable<FabricationType, Integer, Object>> properties = 
				new ArrayList<PropertyUndoable<FabricationType, Integer, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.setShortcut((String) o), 
				i -> i.getShortcut()));
		properties.add(createProperty(
				(i,o) -> i.setValue((String) o), 
				i -> i.getValue()));
		return properties;
	}

}
