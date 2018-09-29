package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.connectors.property.StringPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

public class StringPropertyFilterTable extends PropertyFilterTable<StringProperty, String> {
	
	{
		setItemClass(StringProperty.class);
		
		this.setTableName("String Properties");
	}

	@Override
	protected CrudConnector<StringProperty,?> getPropertyConnector() {
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