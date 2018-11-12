package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbEditFilterTable<T extends DbPersistent<T,?>,R> extends IDbTable<T,R>, IEditFilterTable<T,R>{
	
	default void setFilteredValue(T filteredItem, int filteredCol, R value) {
		getFilteredColumns().get(filteredCol).set(filteredItem, value);
	}
	
	default void setFilteredValue(int row, int col, R value) {
		setFilteredValue(getFilteredItems().get(row), col, value);
	}
	
}
