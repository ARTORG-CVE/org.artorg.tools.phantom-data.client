package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public interface IColumn<ITEM extends DatabasePersistent<ITEM, ?>, 
		CELL_TYPE,
		SUB_ID_TYPE> {
	
	
	CELL_TYPE get(ITEM item);
	
	void set(ITEM item, Object value);
	
	String getColumnName();

}
