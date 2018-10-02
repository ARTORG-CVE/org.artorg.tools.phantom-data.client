package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.server.model.property.StringProperty;

public class StringPropertyFilterTable extends PropertyFilterTable<StringProperty, String> {
	
	{
		this.setTableName("String Properties");
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