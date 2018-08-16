package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public interface IColumn<ITEM extends DatabasePersistent<ITEM, ?>, SUB_ID_TYPE> {
	
	String get(ITEM item);
	
	void set(ITEM item, String value);
	
	String getColumnName();
	
	boolean update(ITEM item);

}
