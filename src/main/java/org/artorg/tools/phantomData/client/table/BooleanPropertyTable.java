package org.artorg.tools.phantomData.client.table;

import java.util.Arrays;
import java.util.List;

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
	public Object getValue(BooleanProperty item, int col) {
		switch (col) {
			case 0: return item.getId();
			case 1: return item.getPropertyField().getDescription();
			case 2: return item.getBool();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValue(BooleanProperty item, int col, Object value) {
		switch (col) {
			case 0: item.setId((Integer) value); break;
			case 1: item.getPropertyField().setDescription((String) value); break;
			case 2: item.setBool((Boolean) value); break;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public List<String> getColumnNames() {
		return Arrays.asList("id", "property field", "value");
	}
	

}
