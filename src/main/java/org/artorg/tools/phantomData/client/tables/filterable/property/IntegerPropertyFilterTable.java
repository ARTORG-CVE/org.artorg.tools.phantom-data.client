package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.IntegerPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;

public class IntegerPropertyFilterTable extends PropertyFilterTable<IntegerProperty, Integer> {
	
	{
		this.setConnector(IntegerPropertyConnector.get());
	}

	@Override
	public String getTableName() {
		return "Integer Properties";
	}

	@Override
	protected HttpConnectorSpring<IntegerProperty> getPropertyConnector() {
		return IntegerPropertyConnector.get();
	}

	@Override
	protected String toString(Integer value) {
		return Integer.toString(value);
	}

	@Override
	protected Integer fromString(String s) {
		return Integer.valueOf(s);
	}

}