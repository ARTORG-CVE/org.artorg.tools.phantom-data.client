package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class FilterTable<TABLE extends Table<TABLE, ITEM, ID_TYPE>, 
		ITEM extends DatabasePersistent<ITEM, ID_TYPE>, 
		ID_TYPE>
		extends Table<TABLE, ITEM, ID_TYPE> {

	private ObservableList<ITEM> filteredItems;
	private Predicate<ITEM> filterPredicate;
	private List<Predicate<ITEM>> columnItemFilterPredicates;
	private List<Predicate<ITEM>> columnTextFilterPredicates;
	private Comparator<ITEM> sortComparator;
	
	{
		filteredItems = FXCollections.observableArrayList();
		filterPredicate = item -> true;
		columnItemFilterPredicates = new ArrayList<Predicate<ITEM>>();
		sortComparator = (i1,i2) -> {
			if (i1.getId() instanceof Integer)
				return ((Integer)i1.getId()).compareTo(((Integer)i2.getId()));
			if (i1.getId() instanceof Long)
				return ((Long)i1.getId()).compareTo(((Long)i2.getId()));
			return i1.getId().toString().compareTo(i2.getId().toString());
		};
	}

	public void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator.compare(valueGetter.apply(item1),valueGetter.apply(item2));
	}
	
	@Override
	public void setConnector(HttpDatabaseCrud<ITEM, ID_TYPE> connector) {
		super.setConnector(connector);
	}
	
	@Override
	public void readAllData() {
		super.readAllData();
		
		columnItemFilterPredicates = new ArrayList<Predicate<ITEM>>(columns.size());
		for (int i=0; i<columns.size(); i++)
			columnItemFilterPredicates.add(item -> true);
		
		columnTextFilterPredicates = new ArrayList<Predicate<ITEM>>(columns.size());
		for (int i=0; i<columns.size(); i++)
			columnTextFilterPredicates.add(item -> true);
		
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
	
	public void setFilteredValue(int row, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		ITEM superItem = items.stream().filter(item -> item.getId().equals(filteredItems.get(row).getId())).findFirst().get();
		setValue(superItem, filteredItems.get(row), col, value, redo, undo);
	}

	public Predicate<ITEM> getFilterPredicate() {
		return filterPredicate;
	}

	public void setColumnItemFilterValues(int columnIndex, List<String> values) {
		columnItemFilterPredicates.set(columnIndex, item -> {
			return values.stream().filter(value -> getValue(item,columnIndex).equals(value)).findFirst().isPresent();
		});
	}
	
	public void setColumnTextFilterValues(int columnIndex, String searchText) {
		final Pattern p = Pattern.compile("(?i)" +searchText);
		if (searchText.isEmpty())
			columnTextFilterPredicates.set(columnIndex, item -> true);
		else
			columnTextFilterPredicates.set(columnIndex, item ->
				p.matcher(getValue(item, columnIndex)).find());
	}
	
	public void applyFilter() {
		Predicate<ITEM> itemFilter = columnItemFilterPredicates.stream().reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
		Predicate<ITEM> textFilter = columnTextFilterPredicates.stream().reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
//		filterPredicate = itemFilter;
		filterPredicate = itemFilter.and(textFilter);
		this.filteredItems = FXCollections.observableArrayList();
		this.filteredItems.addAll(items.stream().filter(filterPredicate).sorted(sortComparator)
				.collect(Collectors.toList()));
	}

}
