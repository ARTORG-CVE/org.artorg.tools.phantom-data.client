package org.artorg.tools.phantomData.client.table;

public interface IEditFilterTable<T> extends IEditTable<T>, IFilterTable<T> {

	default void setFilteredValue(T filteredItem, int filteredCol, Object value) {
		getFilteredColumns().get(filteredCol).set(filteredItem, value);
	}
	
	default void setFilteredValue(int row, int col, Object value) {
		setFilteredValue(getFilteredItems().get(row), col, value);
	}
	
}
