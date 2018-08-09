package org.artorg.tools.phantomData.client.table;

import java.util.Arrays;
import java.util.List;

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
	public Object getValue(AnnulusDiameter item, int col) {
		switch (col) {
			case 0: return item.getId();
			case 1: return item.getShortcut();
			case 2: return item.getValue();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValue(AnnulusDiameter item, int col, Object value) {
		switch (col) {
			case 0: item.setId((Integer) value); break;
			case 1: item.setShortcut((Integer) value); break;
			case 2: item.setValue((Double) value); break;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public List<String> getColumnNames() {
		return Arrays.asList("id", "shortcut", "value");
	}

}
