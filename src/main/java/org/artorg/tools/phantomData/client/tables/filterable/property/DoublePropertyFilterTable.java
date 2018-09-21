package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.property.DoublePropertyConnector;
import org.artorg.tools.phantomData.server.model.property.DoubleProperty;

public class DoublePropertyFilterTable extends PropertyFilterTable<DoubleProperty, Double> {
	
	{
		this.setConnector(DoublePropertyConnector.get());
	}

	@Override
	public String getTableName() {
		return "Double Properties";
	}

	@Override
	protected HttpConnectorSpring<DoubleProperty> getPropertyConnector() {
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