package org.artorg.tools.phantomData.client.table;

public interface IEditFilterTable<T,R> extends IEditTable<T,R>, IFilterTable<T,R> {

	default void setFilteredValue(T filteredItem, int filteredCol, R value) {
		getFilteredColumns().get(filteredCol).set(filteredItem, value);
	}
	
	default void setFilteredValue(int row, int col, R value) {
		setFilteredValue(getFilteredItems().get(row), col, value);
	}
	
}
