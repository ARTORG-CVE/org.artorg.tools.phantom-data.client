package org.artorg.tools.phantomData.client.table;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import javafx.collections.ObservableList;

public interface FilterableTable<ITEM> extends Table<ITEM> {

	int getFilteredNrows();

	String getColumnFilteredValue(int localRow, int col);

	void setColumnItemFilterValues(int col, List<String> selectedValues);

	void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter);

	void setColumnTextFilterValues(int col, String regex);

	String getFilteredValue(int row, int col);

	String getFilteredValue(ITEM item, int col);

	List<String> getFilteredColumnNames();

	int getFilteredNcols();

	void applyFilter();
	
	ObservableList<ITEM> getFilteredItems();

	
}
