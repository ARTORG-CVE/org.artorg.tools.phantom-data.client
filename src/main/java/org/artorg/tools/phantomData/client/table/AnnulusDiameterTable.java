package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;

public class AnnulusDiameterTable extends StageTable<AnnulusDiameterTable, AnnulusDiameter, Integer> {
	
	@Override
	public HttpDatabaseCrud<AnnulusDiameter, Integer> getConnector() {
		return AnnulusDiameterConnector.get();
	}

	@Override
	public List<String> createColumnNames() {
		return Arrays.asList("id", "shortcut", "value");
	}

	@Override
	public List<PropertyUndoable<AnnulusDiameter, Object>> createProperties() {
		List<PropertyUndoable<AnnulusDiameter, Object>> properties = 
				new ArrayList<PropertyUndoable<AnnulusDiameter, Object>>();
		properties.add(createProperty(
				(i,o) -> i.setId((Integer) o), 
				i -> i.getId()));
		properties.add(createProperty(
				(i,o) -> i.setShortcut((Integer) o), 
				i -> i.getShortcut()));
		properties.add(createProperty(
				(i,o) -> i.setValue((Double) o), 
				i -> i.getValue()));
		return properties;
	}

}
