package org.artorg.tools.phantomData.client.table;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;

import javafx.collections.ObservableList;

public interface IFilterTable<ITEM,R> extends ITable<ITEM,R> {

	int getFilteredNrows();

	void setColumnItemFilterValues(int col, List<R> selectedValues);

	void setSortComparator(Comparator<R> sortComparator, Function<ITEM, R> valueGetter);

	void setColumnTextFilterValues(int col, String regex);

	R getFilteredValue(ITEM item, int col);

	List<String> getFilteredColumnNames();

	int getFilteredNcols();

	void applyFilter();
	
	ObservableList<ITEM> getFilteredItems();

	List<AbstractColumn<ITEM,? extends R>> getFilteredColumns();
	
	List<AbstractFilterColumn<ITEM,? extends R>> getFilteredFilterColumns();

	Predicate<ITEM> getFilterPredicate();
	
	void setSortComparatorQueue(Queue<Comparator<ITEM>> sortComparatorQueue);
	
	Queue<Comparator<ITEM>> getSortComparatorQueue();
	
	default R getFilteredValue(int row, int col) {
		return getFilteredColumns().get(col).get(getFilteredItems().get(row));
	}
	
	default R getColumnFilteredValue(int row, int col) {
		List<AbstractColumn<ITEM,? extends R>> columns = getFilteredColumns();
		AbstractColumn<ITEM,?> column = columns.get(col);
		ObservableList<ITEM> items = getItems();
		ITEM item = items.get(row);
		column.get(item);
		return getFilteredColumns().get(col).get(getItems().get(row));
	}
	
}
