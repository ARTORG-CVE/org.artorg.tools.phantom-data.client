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
	private int nFilteredCols;
	private List<Integer> mappedColumnIndexes;
	private Function<Integer, Integer> columnIndexMapper;
	
	
	{
		filteredItems = FXCollections.observableArrayList();
		filterPredicate = item -> true;
		columnItemFilterPredicates = new ArrayList<Predicate<ITEM>>();
		columnTextFilterPredicates = new ArrayList<Predicate<ITEM>>();
		sortComparator = (i1,i2) -> {
			if (i1.getId() instanceof Integer)
				return ((Integer)i1.getId()).compareTo(((Integer)i2.getId()));
			if (i1.getId() instanceof Long)
				return ((Long)i1.getId()).compareTo(((Long)i2.getId()));
			return i1.getId().toString().compareTo(i2.getId().toString());
		};
		mappedColumnIndexes = new ArrayList<>();
		
	}
	
	public void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator.compare(valueGetter.apply(item1),valueGetter.apply(item2));
	}
	
	@Override
	public void setConnector(HttpDatabaseCrud<ITEM, ID_TYPE> connector) {
		super.setConnector(connector);
		int nCols = getNcols();
		
		mappedColumnIndexes = new ArrayList<Integer>(nCols);
		List<IColumn<ITEM, ?>> columns = super.getColumns();
		for (int i=0; i<nCols; i++)
			if (columns.get(i).isVisible())
				mappedColumnIndexes.add(i);
		nFilteredCols = mappedColumnIndexes.size();
		for (int i=0; i<nCols; i++) {
			columnItemFilterPredicates.add(item -> true);
			columnTextFilterPredicates.add(item -> true);
		}
		columnIndexMapper = i -> mappedColumnIndexes.get(i);
		
	}
	
	@Override
	public ObservableList<ITEM> getItems() {
		return filteredItems;
	}
	
	public int getFilteredNrows() {
		return filteredItems.size();
	}
	
	public int getFilteredNcols() {
		return nFilteredCols;
	}
	
	public List<IColumn<ITEM, ?>> getFilteredColumns() {
		return mappedColumnIndexes.stream().map(i -> getColumns().get(i)).collect(Collectors.toList());
	}
	
	public List<String> getFilteredColumnNames() {
		return getFilteredColumns().stream().map(c -> c.getColumnName())
				.collect(Collectors.toList());
	}
	
	private void setFilteredValue(ITEM item, ITEM filteredItem, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		String currentValue = getFilteredValue(item, col);
		if (value.equals(currentValue))  return;
		
		UndoRedoNode node = new UndoRedoNode(() -> {
				getFilteredColumns().get(col).set(item, value);
				getFilteredColumns().get(col).set(filteredItem, value);
				redo.accept(value);
			}, () -> {
				getFilteredColumns().get(col).set(item, currentValue);
				getFilteredColumns().get(col).set(filteredItem, currentValue);
				undo.accept(currentValue);
			}, () -> getColumns().get(col).update(item));
		undoManager.addAndRun(node);
	}
	
	public String getFilteredValue(ITEM item, int col) {
		return getValue(item, columnIndexMapper.apply(col));
	}
	
	public String getFilteredValue(int row, int col) {
		return getFilteredColumns().get(col).get(filteredItems.get(row));
	}
	
	public String getColumnFilteredValue(int row, int col) {
		List<IColumn<ITEM, ?>> columns = getFilteredColumns();
		IColumn<ITEM, ?> column = columns.get(col);
		ObservableList<ITEM> items = super.getItems();
		ITEM item = items.get(row);
		column.get(item);
		return getFilteredColumns().get(col).get(super.getItems().get(row));
	}
	
	public void setFilteredValue(int row, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		ITEM superItem = items.stream().filter(item -> item.getId().equals(filteredItems.get(row).getId())).findFirst().get();
		setFilteredValue(superItem, filteredItems.get(row), col, value, redo, undo);
	}

	public Predicate<ITEM> getFilterPredicate() {
		return filterPredicate;
	}

	public void setColumnItemFilterValues(int columnIndex, List<String> values) {
		columnItemFilterPredicates.set(columnIndexMapper.apply(columnIndex), item -> {
			return values.stream().filter(value -> getFilteredValue(item,columnIndex).equals(value)).findFirst().isPresent();
		});
	}
	
	public void setColumnTextFilterValues(int columnIndex, String searchText) {
		System.out.println(String.format("columnIndex: %d, mappedColIndex: %d, searchText: %s" 
				,columnIndex, columnIndexMapper.apply(columnIndex), searchText));
		
		final Pattern p = Pattern.compile("(?i)" +searchText);
		if (searchText.isEmpty())
			columnTextFilterPredicates.set(columnIndexMapper.apply(columnIndex), item -> true);
		else
			columnTextFilterPredicates.set(columnIndexMapper.apply(columnIndex), item ->
				p.matcher(getFilteredValue(item, columnIndex)).find());
	}
	
	public void applyFilter() {
//		Predicate<ITEM> itemFilter = mappedColumnIndexes.stream()
//				.filter(i -> i<columnItemFilterPredicates.size())
//				.map(i -> columnItemFilterPredicates.get(i))
//				.reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
//		Predicate<ITEM> textFilter = mappedColumnIndexes.stream()
//				.filter(i -> i<columnTextFilterPredicates.size())
//				.map(i -> columnTextFilterPredicates.get(i))
//				.reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
		
//		for (int i=0; i<nFilteredCols; i++) {
//			columnItemFilterPredicates.get(columnIndexMapper.apply(i))
//		}
//		
		Predicate<ITEM> itemFilter = columnItemFilterPredicates.stream().reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
		Predicate<ITEM> textFilter = columnTextFilterPredicates.stream().reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
//		
		
//		filterPredicate = itemFilter;
		filterPredicate = itemFilter.and(textFilter);
		this.filteredItems = FXCollections.observableArrayList();
		this.filteredItems.addAll(items.stream().filter(filterPredicate).sorted(sortComparator)
				.collect(Collectors.toList()));
	}

}
