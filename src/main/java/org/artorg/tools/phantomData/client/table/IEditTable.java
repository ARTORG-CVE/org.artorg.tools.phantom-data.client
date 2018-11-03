package org.artorg.tools.phantomData.client.table;

public interface IEditTable<T> extends ITable<T> {
	
	default void setValue(T item, int col, String value) {
		getColumns().get(col).set(item, value);
	}
	
	default void setValue(int row, int col, String value) {
		setValue(getItems().get(row), col, value);
	}

}
