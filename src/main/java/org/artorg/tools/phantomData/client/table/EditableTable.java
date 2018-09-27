package org.artorg.tools.phantomData.client.table;

import javafx.collections.ObservableList;

public interface EditableTable<ITEM> extends Table<ITEM> {
	
	void setItems(ObservableList<ITEM> items);
	
	default void setValue(ITEM item, int col, String value) {
		getColumns().get(col).set(item, value);
	}
	
	default void setValue(int row, int col, String value) {
		setValue(getItems().get(row), col, value);
	}

}
