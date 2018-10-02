package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;

public class IntegerPropertyFilterTable extends PropertyFilterTable<IntegerProperty, Integer> {
	
	{
		setItemClass(IntegerProperty.class);
		
		this.setTableName("Integer Properties");
	}

	@Override
	protected ICrudConnector<IntegerProperty,?> getPropertyConnector() {
		return HttpConnectorSpring.getOrCreate(IntegerProperty.class);
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