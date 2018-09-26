package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyFilterTable extends PropertyFilterTable<BooleanProperty, Boolean> {
	
	{
		setItemClass(BooleanProperty.class);
	}
	
	@Override
	public String getTableName() {
		return "Boolean Properties";
	}

	@Override
	protected HttpConnectorSpring<BooleanProperty> getPropertyConnector() {
		return BooleanPropertyConnector.get();
	}

	@Override
	protected String toString(Boolean value) {
		return Boolean.toString(value);
	}

	@Override
	protected Boolean fromString(String s) {
		return Boolean.valueOf(s);
	}

}