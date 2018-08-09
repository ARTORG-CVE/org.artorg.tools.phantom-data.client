package org.artorg.tools.phantomData.client.table;

import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.specification.StageTable;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class PropertyFieldTable extends StageTable<PropertyFieldTable,PropertyField, Integer> {
	
//	public static Collection<TableColumn<PropertyField,?>> createUpperTableColumns() {
//		List<TableColumn<PropertyField,?>> list = new ArrayList<TableColumn<PropertyField,?>>();
//		TableColumn<PropertyField, String> descriptionCol = new TableColumn<PropertyField, String>("description");
//		descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getDescription())));
//		list.add(descriptionCol);
//		return list;
//	}
	
	@Override
	public HttpDatabaseCrud<PropertyField, Integer> getConnector() {
		return PropertyFieldConnector.get();
	}

	@Override
	public Object getValue(PropertyField item, int col) {
		switch (col) {
			case 0: return item.getId();
			case 1: return item.getName();
			case 2: return item.getDescription();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValue(PropertyField item, int col, Object value) {
		switch (col) {
			case 0: item.setId((Integer) value); break;
			case 1: item.setName((String) value); break;
			case 2: item.setDescription( (String) value); break;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public List<String> getColumnNames() {
		return Arrays.asList("id", "name", "description");
	}

}
