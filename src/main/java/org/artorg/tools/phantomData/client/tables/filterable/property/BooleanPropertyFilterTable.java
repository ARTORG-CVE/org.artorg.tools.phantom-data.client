package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyFilterTable extends PropertyFilterTable<BooleanProperty, Boolean> {
	
	{
		setItemClass(BooleanProperty.class);
		
		this.setTableName("Boolean Properties");
	}

	@Override
	protected ICrudConnector<BooleanProperty,?> getPropertyConnector() {
		return HttpConnectorSpring.getOrCreate(BooleanProperty.class);
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