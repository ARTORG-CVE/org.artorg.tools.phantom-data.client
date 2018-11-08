package org.artorg.tools.phantomData.client.table;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.collections.ObservableList;

public interface IFilterTable<ITEM> extends ITable<ITEM> {

	int getFilteredNrows();

	void setColumnItemFilterValues(int col, List<Object> selectedValues);

	void setSortComparator(Comparator<Object> sortComparator, Function<ITEM, Object> valueGetter);

	void setColumnTextFilterValues(int col, String regex);

	Object getFilteredValue(ITEM item, int col);

	List<String> getFilteredColumnNames();

	int getFilteredNcols();

	void applyFilter();
	
	ObservableList<ITEM> getFilteredItems();

	List<AbstractFilterColumn<ITEM,?>> getFilteredColumns();

	Predicate<ITEM> getFilterPredicate();
	
	void setSortComparatorQueue(Queue<Comparator<ITEM>> sortComparatorQueue);
	
	Queue<Comparator<ITEM>> getSortComparatorQueue();
	
	default Object getFilteredValue(int row, int col) {
		return getFilteredColumns().get(col).get(getFilteredItems().get(row));
	}
	
	default Object getColumnFilteredValue(int row, int col) {
		List<AbstractFilterColumn<ITEM,?>> columns = getFilteredColumns();
		AbstractColumn<ITEM,?> column = columns.get(col);
		ObservableList<ITEM> items = getItems();
		ITEM item = items.get(row);
		column.get(item);
		return getFilteredColumns().get(col).get(getItems().get(row));
	}
	
}
