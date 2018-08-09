package org.artorg.tools.phantomData.client.table;

import java.util.Arrays;
import java.util.List;

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
	public Object getValue(FabricationType item, int col) {
		switch (col) {
			case 0: return item.getId();
			case 1: return item.getShortcut();
			case 2: return item.getFabricationType();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValue(FabricationType item, int col, Object value) {
		switch (col) {
			case 0: item.setId((Integer) value); break;
			case 1: item.setShortcut((String) value); break;
			case 2: item.setFabricationType( (String) value); break;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public List<String> getColumnNames() {
		return Arrays.asList("id", "shortcut", "value");
	}

}
