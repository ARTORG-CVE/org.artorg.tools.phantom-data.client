package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class LiteratureBaseTable extends StageTable<LiteratureBaseTable, LiteratureBase, Integer> {
	
	@Override
	public HttpDatabaseCrud<LiteratureBase, Integer> getConnector() {
		return LiteratureBaseConnector.get();
	}

	@Override
	public Object getValue(LiteratureBase item, int col) {
		switch (col) {
			case 0: return item.getId();
			case 1: return item.getShortcut();
			case 2: return item.getLiteratureBase();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValue(LiteratureBase item, int col, Object value) {
		switch (col) {
			case 0: item.setId((Integer) value); break;
			case 1: item.setShortcut((String) value); break;
			case 2: item.setLiteratureBase((String) value); break;
		}
		throw new IllegalArgumentException();
		
	}

	@Override
	public List<String> getColumnNames() {
		return Arrays.asList("id", "shortcut", "value");
	}

}
