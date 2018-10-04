package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbEditFilterTable<ITEM extends DbPersistent<ITEM,?>> extends IDbTable<ITEM>, IEditFilterTable<ITEM>{
	
	default void setFilteredValue(ITEM filteredItem, int filteredCol, String value) {
		getFilteredColumns().get(filteredCol).set(filteredItem, value);
	}
	
	default void setFilteredValue(int row, int col, String value) {
		setFilteredValue(getFilteredItems().get(row), col, value);
	}
	
}
