package org.artorg.tools.phantomData.client.tables;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.table.Column;
import org.artorg.tools.phantomData.client.table.IColumn;
import org.artorg.tools.phantomData.client.table.StageTable;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class BooleanPropertyTable extends StageTable<BooleanPropertyTable, BooleanProperty, Integer> {
	
	{
		this.setConnector(BooleanPropertyConnector.get());
	}

	@Override
	public List<IColumn<BooleanProperty, ?, ?>> createColumns() {
		List<IColumn<BooleanProperty, ?, ?>> columns =
				new ArrayList<IColumn<BooleanProperty, ?, ?>>();
		columns.add(new Column<BooleanProperty, BooleanProperty, Integer, Integer>(
				"id", item -> item, 
				path -> path.getId(), 
				(path,value) -> path.setId((Integer) value),
				BooleanPropertyConnector.get()));
		columns.add(new Column<BooleanProperty, PropertyField, String, Integer>(
				"property field", item -> item.getPropertyField(), 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription((String) value),
				PropertyFieldConnector.get()));
		columns.add(new Column<BooleanProperty, BooleanProperty, Boolean, Integer>(
				"value", item -> item, 
				path -> path.getBool(), 
				(path,value) -> path.setBool((Boolean) value),
				BooleanPropertyConnector.get()));
		return columns;
	}

}
