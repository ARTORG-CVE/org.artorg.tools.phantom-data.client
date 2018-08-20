package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class FilterTable<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE>
		extends Table<TABLE, ITEM, ID_TYPE> {

	private final ObservableList<ITEM> filteredItems;
	private Predicate<ITEM> filterPredicate;
	private List<Predicate<ITEM>> columnFilterPredicates;
	private Comparator<? super ITEM> sortComparator;
	

	public Comparator<? super ITEM> getSortComparator() {
		return sortComparator;
	}

	public void setSortComparator(Comparator<? super ITEM> sortComparator) {
		this.sortComparator = sortComparator;
	}
	
	

	{
		filteredItems = FXCollections.observableArrayList();
		filterPredicate = item -> true;
		columnFilterPredicates = new ArrayList<Predicate<ITEM>>();
		sortComparator = (i1,i2) -> {
			if (i1.getId() instanceof Integer)
				return ((Integer)i1.getId()).compareTo(((Integer)i2.getId()));
			if (i1.getId() instanceof Long)
				return ((Long)i1.getId()).compareTo(((Long)i2.getId()));
			return i1.getId().toString().compareTo(i2.getId().toString());
		};
	}
	
	public void createColumnFilters() {
//		columnFilters = new ArrayList<Predicate<ITEM>>(columns.size());
//		filterPredicate = columnFilters.stream().reduce((f1,f2) -> f1.and(f2)).get();
	}
	
	@Override
	public void setConnector(HttpDatabaseCrud<ITEM, ID_TYPE> connector) {
		super.setConnector(connector);
		createColumnFilters();
	}
	
	@Override
	public void readAllData() {
		super.readAllData();
		
		
		columnFilterPredicates = new ArrayList<Predicate<ITEM>>(columns.size());
		for (int i=0; i<columns.size(); i++)
			columnFilterPredicates.add(item -> true);
		
		
		
		applyFilter();
	}
	
	@Override
	public ObservableList<ITEM> getItems() {
		return filteredItems;
	}
	
	public int getFilteredNrows() {
		return filteredItems.size();
	}
	
	private void setValue(ITEM item, ITEM filteredItem, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		String currentValue = getValue(item, col);
		if (value.equals(currentValue))  return;
		
		UndoRedoNode node = new UndoRedoNode(() -> {
				columns.get(col).set(item, value);
				columns.get(col).set(filteredItem, value);
				redo.accept(value);
			}, () -> {
				columns.get(col).set(item, currentValue);
				columns.get(col).set(filteredItem, currentValue);
				undo.accept(currentValue);
			}, () -> columns.get(col).update(item));
		undoManager.addAndRun(node);
	}
	
	public String getFilteredValue(int row, int col) {
		return columns.get(col).get(filteredItems.get(row));
	}
	
	
	@Override
	public void setValue(int row, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		ITEM superItem = items.stream().filter(item -> item.getId().equals(filteredItems.get(row).getId())).findFirst().get();
		setValue(superItem, filteredItems.get(row), col, value, redo, undo);
	}

	public Predicate<ITEM> getFilterPredicate() {
		return filterPredicate;
	}

	public void setColumnFilterValues(int columnIndex, List<String> values) {
		columnFilterPredicates.set(columnIndex, item -> {
			return values.stream().filter(value -> getValue(item,columnIndex).equals(value)).findFirst().isPresent();
		});
	}
	
	public void applyFilter() {
		filterPredicate = columnFilterPredicates.stream().reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
		this.filteredItems.clear();
		this.filteredItems.addAll(items.stream().filter(filterPredicate).sorted(sortComparator)
				.collect(Collectors.toList()));
	}

	

	
	
	
	
	

}
