package org.artorg.tools.phantomData.client.tables.filterable.property;

import org.artorg.tools.phantomData.server.model.property.BooleanProperty;

public class BooleanPropertyFilterTable extends PropertyFilterTable<BooleanProperty, Boolean> {
	
	{	
		this.setTableName("Boolean Properties");
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