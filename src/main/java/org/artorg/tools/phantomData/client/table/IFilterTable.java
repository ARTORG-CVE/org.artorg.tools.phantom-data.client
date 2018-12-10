package org.artorg.tools.phantomData.client.table;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.client.table.columns.AbstractColumn;
import org.artorg.tools.phantomData.client.table.columns.AbstractFilterColumn;

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
	
//	default void setFilteredValue(int row, int col, R value, Consumer<R> redo, Consumer<R> undo) {
//		ITEM superItem = getItems().stream().filter(item -> item.getId().equals(getFilteredItems().get(row).getId())).findFirst().get();
//		ITEM filteredItem = getFilteredItems().get(row);
//		R currentValue = getFilteredValue(superItem, col);
//		if (value.equals(currentValue))  return;
//		
//		UndoRedoNode node = new UndoRedoNode(() -> {
//				getFilteredColumns().get(col).set(filteredItem, value);
//				redo.accept(value);
//			}, () -> {
//				getFilteredColumns().get(col).set(filteredItem, currentValue);
//				undo.accept(currentValue);
//			}, () -> {
//				getFilteredColumns().get(col).update(filteredItem);
//		});
//		
//		getUndoManager().addAndRun(node);
//		
//	}
//	
//	default void setFilteredValue(ITEM item, int filteredCol, R value, Consumer<R> redo, Consumer<R> undo) {
//		ITEM superItem = getItems().stream().filter(i -> i.getId().equals(item.getId())).findFirst().get();
//		R currentValue = getFilteredValue(superItem, filteredCol);
//		if (value.equals(currentValue))  return;
//		
//		UndoRedoNode node = new UndoRedoNode(() -> {
//				getFilteredColumns().get(filteredCol).set(item, value);
//				redo.accept(value);
//			}, () -> {
//				getFilteredColumns().get(filteredCol).set(item, currentValue);
//				undo.accept(currentValue);
//			}, () -> {
//				getFilteredColumns().get(filteredCol).update(item);
//		});
//		
//		
//		getUndoManager().addAndRun(node);
//	}	
	
	default void setFilteredValue(ITEM filteredItem, int filteredCol, R value) {
		if (!isEditable()) return;
		if (!isFilterable()) {
		getFilteredColumns().get(filteredCol).set(filteredItem, value);
		return;
		}
//		setFilteredValue(filteredItem, filteredCol, value, s -> {}, s -> {});
	}
	
	default void setFilteredValue(int row, int col, R value) {
		if (!isEditable()) return;
		setFilteredValue(getFilteredItems().get(row), col, value);
	}
	
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
