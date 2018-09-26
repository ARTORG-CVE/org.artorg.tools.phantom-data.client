package org.artorg.tools.phantomData.client.scene.control;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;

import javafx.collections.ObservableList;

public interface FilterableTable<ITEM> {

	int getNrows();

	String getColumnFilteredValue(int localRow, int col);

	void setColumnItemFilterValues(int col, List<String> selectedValues);

	void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter);

	void setColumnTextFilterValues(int col, String regex);

	String getFilteredValue(int row, int col);

	String getFilteredValue(ITEM item, int col);

	List<String> getFilteredColumnNames();

	int getFilteredNcols();

	void applyFilter();

	ObservableList<ITEM> getItems();

	void readAllData();

	HttpConnectorSpring<ITEM> getConnector();

	void setFilteredValue(ITEM item, int localCol, String newValue);

	String getTableName();

	String getItemName();
	
	void setItems(ObservableList<ITEM> items);

	
}
