package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.server.model.property.IntegerProperty;

public class IntegerPropertyFilterTable extends PropertyFilterTable<IntegerProperty, Integer> {
	
	{	
		this.setTableName("Integer Properties");
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