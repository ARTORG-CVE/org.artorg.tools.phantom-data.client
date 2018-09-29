package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.client.connectors.property.BooleanPropertyConnector;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyFilterTable extends PropertyFilterTable<BooleanProperty, Boolean> {
	
	{
		setItemClass(BooleanProperty.class);
		
		this.setTableName("Boolean Properties");
	}

	@Override
	protected CrudConnector<BooleanProperty,?> getPropertyConnector() {
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