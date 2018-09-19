package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.StringPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

public class StringPropertyFilterTable extends PropertyFilterTable<StringProperty, String> {
	
	{
		this.setConnector(StringPropertyConnector.get());
	}

	@Override
	public String getTableName() {
		return "String Properties";
	}

	@Override
	protected HttpConnectorSpring<StringProperty> getPropertyConnector() {
		return StringPropertyConnector.get();
	}

	@Override
	protected String toString(String value) {
		return value;
	}

	@Override
	protected String fromString(String s) {
		return s;
	}

}