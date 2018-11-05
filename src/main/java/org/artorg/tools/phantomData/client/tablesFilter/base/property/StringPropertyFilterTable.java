package org.artorg.tools.phantomData.client.tablesFilter.base.property;

import org.artorg.tools.phantomData.server.model.base.property.StringProperty;

public class StringPropertyFilterTable extends PropertyFilterTable<StringProperty, String> {
	
	{
		setTableName("String Properties");
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