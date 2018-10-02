package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

public class DoublePropertyFilterTable extends PropertyFilterTable<DoubleProperty, Double> {
	
	{
		setItemClass(DoubleProperty.class);
		
		this.setTableName("Double Properties");
	}
	
	@Override
	protected ICrudConnector<DoubleProperty,?> getPropertyConnector() {
		return HttpConnectorSpring.getOrCreate(DoubleProperty.class);
	}

	@Override
	protected String toString(Double value) {
		return Double.toString(value);
	}

	@Override
	protected Double fromString(String s) {
		return Double.valueOf(s);
	}
	
}