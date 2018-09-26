package org.artorg.tools.phantomData.client.table;

import javafx.collections.ObservableList;

public interface ITableEditable<ITEM> {
	
	void setFilteredValue(ITEM item, int localCol, String newValue);
	
	void setItems(ObservableList<ITEM> items);

}
