package org.artorg.tools.phantomData.client.tablesFilter.base.property;

import org.artorg.tools.phantomData.server.model.base.property.IntegerProperty;

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