package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.connectors.property.DoublePropertyConnector;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

public class DoublePropertyFilterTable extends PropertyFilterTable<DoubleProperty, Double> {
	
	{
		setItemClass(DoubleProperty.class);
		
		this.setTableName("Double Properties");
	}
	
	@Override
	protected ICrudConnector<DoubleProperty,?> getPropertyConnector() {
		return DoublePropertyConnector.get();
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