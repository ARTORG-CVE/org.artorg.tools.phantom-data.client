package org.artorg.tools.phantomData.client.tables.filterable.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.PropertyFieldConnector;
import org.artorg.tools.phantomData.client.scene.control.table.Column;
import org.artorg.tools.phantomData.client.scene.control.table.FilterTableSpringDb;
import org.artorg.tools.phantomData.client.scene.control.table.IColumn;
import org.artorg.tools.phantomData.server.model.property.Property;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

public abstract class PropertyFilterTable<ITEM extends Property<VALUE, ID_TYPE>, VALUE extends Comparable<VALUE>, ID_TYPE> 
		extends FilterTableSpringDb<ITEM, ID_TYPE> {

	@Override
	public List<IColumn<ITEM, ?>> createColumns() {
		List<IColumn<ITEM, ?>> columns =
				new ArrayList<IColumn<ITEM, ?>>();
		columns.add(new Column<ITEM, ITEM, ID_TYPE>(
				"id", item -> item, 
				path -> String.valueOf(path.getId()), 
				(path,value) -> path.setId(fromStringToId(value)),
				getPropertyConnector()));
		columns.add(new Column<ITEM, PropertyField, Integer>(
				"property field", item -> item.getPropertyField(), 
				path -> path.getDescription(), 
				(path,value) -> path.setDescription(value),
				PropertyFieldConnector.get()));
		columns.add(new Column<ITEM, ITEM, ID_TYPE>(
				"value", item -> item, 
				path -> String.valueOf(path.getValue()), 
				(path,value) -> path.setValue(fromString(value)),
				getPropertyConnector()));
		return columns;
	}
	
	protected abstract HttpConnectorSpring<ITEM, ID_TYPE> getPropertyConnector();
	
	protected abstract String toString(VALUE value);
	
	protected abstract VALUE fromString(String s);
	
	protected abstract ID_TYPE fromStringToId(String s);

}