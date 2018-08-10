package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.connector.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseTable extends StageTable<LiteratureBaseTable, LiteratureBase, Integer> {
	
	@Override
	public HttpDatabaseCrud<LiteratureBase, Integer> getConnector() {
		return LiteratureBaseConnector.get();
	}

	@Override
	public List<String> createColumnNames() {
		return Arrays.asList("id", "shortcut", "value");
	}

	@Override
	public List<PropertyUndoable<LiteratureBase, Object>> createProperties() {
		List<PropertyUndoable<LiteratureBase, Object>> properties = 
				new ArrayList<PropertyUndoable<LiteratureBase, Object>>();
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
