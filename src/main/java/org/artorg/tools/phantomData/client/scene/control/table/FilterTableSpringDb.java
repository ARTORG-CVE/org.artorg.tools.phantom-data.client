package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.Connectors;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class FilterTableSpringDb<ITEM extends DbPersistent> extends TableSpringDb<ITEM> {
	private ObservableList<ITEM> filteredItems;
	private Predicate<ITEM> filterPredicate;
	private List<Predicate<ITEM>> columnItemFilterPredicates;
	private List<Predicate<ITEM>> columnTextFilterPredicates;
	private Comparator<ITEM> sortComparator;
	private int nFilteredCols;
	private List<Integer> mappedColumnIndexes;
	private Function<Integer, Integer> columnIndexMapper;
	private Class<ITEM> itemClass;
	
	{
		filteredItems = FXCollections.observableArrayList();
		filterPredicate = item -> true;
		columnItemFilterPredicates = new ArrayList<Predicate<ITEM>>();
		columnTextFilterPredicates = new ArrayList<Predicate<ITEM>>();
		sortComparator = (i1,i2) -> i1.getId().compareTo(i2.getId()); 
		mappedColumnIndexes = new ArrayList<>();
		
	}
	
	public FilterTableSpringDb(Class<ITEM> itemClass) {
		this.itemClass = itemClass;
		this.setConnector(Connectors.getConnector(itemClass));
	}
	
	public void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator.compare(valueGetter.apply(item1),valueGetter.apply(item2));
	}
	
	@Override
	public void setConnector(HttpConnectorSpring<ITEM> connector) {
		super.setConnector(connector);
		int nCols = getNcols();
		
		mappedColumnIndexes = new ArrayList<Integer>(nCols);
		List<IColumn<ITEM>> columns = super.getColumns();
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
	public void readAllData() {
		super.readAllData();
		filteredItems.clear();
		filteredItems.addAll(super.getItems());
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
	
	public List<IColumn<ITEM>> getFilteredColumns() {
		return mappedColumnIndexes.stream().map(i -> getColumns().get(i)).collect(Collectors.toList());
	}
	
	public List<String> getFilteredColumnNames() {
		return getFilteredColumns().stream().map(c -> c.getColumnName())
				.collect(Collectors.toList());
	}
	
	private void setFilteredValue(ITEM item, ITEM filteredItem, int filteredCol, String value, Consumer<String> redo, Consumer<String> undo) {
		String currentValue = getFilteredValue(item, filteredCol);
		if (value.equals(currentValue))  return;
		
		UndoRedoNode node = new UndoRedoNode(() -> {
				getFilteredColumns().get(filteredCol).set(filteredItem, value);
				redo.accept(value);
			}, () -> {
				getFilteredColumns().get(filteredCol).set(filteredItem, currentValue);
				undo.accept(currentValue);
			}, () -> {
				getFilteredColumns().get(filteredCol).update(filteredItem);
		});
		undoManager.addAndRun(node);
	}
	
	public String getFilteredValue(ITEM item, int col) {
		return getValue(item, columnIndexMapper.apply(col));
	}
	
	public String getFilteredValue(int row, int col) {
		return getFilteredColumns().get(col).get(filteredItems.get(row));
	}
	
	public String getColumnFilteredValue(int row, int col) {
		List<IColumn<ITEM>> columns = getFilteredColumns();
		IColumn<ITEM> column = columns.get(col);
		ObservableList<ITEM> items = super.getItems();
		ITEM item = items.get(row);
		column.get(item);
		return getFilteredColumns().get(col).get(super.getItems().get(row));
	}
	
	public void setFilteredValue(int row, int col, String value, Consumer<String> redo, Consumer<String> undo) {
		ITEM superItem = items.stream().filter(item -> item.getId().equals(filteredItems.get(row).getId())).findFirst().get();
		setFilteredValue(superItem, filteredItems.get(row), col, value, redo, undo);
	}
	
	public void setFilteredValue(ITEM item, int filteredCol, String value, Consumer<String> redo, Consumer<String> undo) {
		ITEM superItem = items.stream().filter(i -> i.getId().equals(item.getId())).findFirst().get();
		setFilteredValue(superItem, item, filteredCol, value, redo, undo);
	}
	
	public void setFilteredValue(ITEM item, int filteredCol, String value) {
		setFilteredValue(item, filteredCol, value, s -> {}, s -> {});
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
		final Pattern p = Pattern.compile("(?i)" +searchText);
		if (searchText.isEmpty())
			columnTextFilterPredicates.set(columnIndexMapper.apply(columnIndex), item -> true);
		else
			columnTextFilterPredicates.set(columnIndexMapper.apply(columnIndex), item ->
				p.matcher(getFilteredValue(item, columnIndex)).find());
	}
	
	public void applyFilter() {
		Predicate<ITEM> itemFilter = mappedColumnIndexes.stream()
				.filter(i -> i<columnItemFilterPredicates.size())
				.map(i -> columnItemFilterPredicates.get(i))
				.reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
		Predicate<ITEM> textFilter = mappedColumnIndexes.stream()
				.filter(i -> i<columnTextFilterPredicates.size())
				.map(i -> columnTextFilterPredicates.get(i))
				.reduce((f1,f2) -> f1.and(f2)).orElse(item -> true);
		filterPredicate = itemFilter.and(textFilter);
		this.filteredItems.clear();
		this.filteredItems.addAll(items.stream().filter(filterPredicate).sorted(sortComparator)
				.collect(Collectors.toList()));
	}
	
	@Override
	public String toString() {
		int nRows = this.getFilteredNrows();
		int nCols = this.getFilteredNcols();
		if (nRows == 0 || nCols == 0) return "";
		String[][] content = new String[nRows+1][nCols];
				
		for (int col=0; col<nCols; col++)
			content[0][col] = getFilteredColumnNames().get(col);
		
		for(int row=0; row<nRows; row++) 
			for(int col=0; col<nCols; col++)
				content[row+1][col] = this.getFilteredValue(row, col);
		
		int[] columnWidth = new int[nCols];
		for(int col=0; col<nCols; col++) {
			int maxLength = 0;
			for(int row=0; row<nRows; row++) {
				if (content[row][col].length() > maxLength)
					maxLength = content[row][col].length();
			}
			columnWidth[col] = maxLength;
		}
		
		List<String> columnStrings = new ArrayList<String>();
		
		for(int col=0; col<nCols; col++) {
			content[0][col] = content[0][col] +StringUtils
					.repeat(" ", columnWidth[col] - content[0][col].length());	
		}
		columnStrings.add(Arrays.stream(content[0]).collect(Collectors.joining("|")));
		columnStrings.add(StringUtils.repeat("-", columnStrings.get(0).length()));
		
		
		for(int row=0; row<nRows; row++) {
			for(int j=0; j<nCols; j++)
				content[row+1][j] = content[row+1][j] +StringUtils
					.repeat(" ", columnWidth[j] - content[row+1][j].length());
			columnStrings.add(Arrays.stream(content[row+1]).collect(Collectors.joining("|")));
		}
		
		return columnStrings.stream().collect(Collectors.joining("\n"));
	}

}
