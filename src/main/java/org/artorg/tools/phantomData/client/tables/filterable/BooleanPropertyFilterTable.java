package org.artorg.tools.phantomData.client.tables.filterable;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public class BooleanPropertyFilterTable extends FilterTableSpringDb<BooleanProperty, Integer> {
	
	{
		this.setConnector(BooleanPropertyConnector.get());
	}

	@Override
	public List<IColumn<BooleanProperty, ?>> createColumns() {
		List<IColumn<BooleanProperty, ?>> columns =
				new ArrayList<IColumn<BooleanProperty, ?>>();
		columns.add(new Column<BooleanProperty, BooleanProperty, Integer>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(Integer.valueOf(value)),
				BooleanPropertyConnector.get()));
		columns.add(new Column<BooleanProperty, PropertyField, Integer>(
				"property field", item -> item.getPropertyField(), 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription(value),
				PropertyFieldConnector.get()));
		columns.add(new Column<BooleanProperty, BooleanProperty, Integer>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(Boolean.valueOf(value)),
				BooleanPropertyConnector.get()));
		return columns;
	}

	@Override
	public String getTableName() {
		return "Boolean Properties";
	}

}
