package org.artorg.tools.phantomData.client.table;

public interface IEditTable<T,R> extends ITable<T,R> {
	
	default void setValue(T item, int col, R value) {
		getColumns().get(col).set(item, value);
	}
	
	default void setValue(int row, int col, R value) {
		setValue(getItems().get(row), col, value);
	}

}
