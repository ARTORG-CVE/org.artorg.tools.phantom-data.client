package org.artorg.tools.phantomData.client.table;

import static org.artorg.tools.phantomData.client.util.StreamUtils.castFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.LimitedQueue;
import org.artorg.tools.phantomData.server.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@SuppressWarnings("unchecked")
public class TableBase<T> {
	private final ObservableList<T> items;
	private List<AbstractColumn<T, ? extends Object>> columns;
	private String tableName;
	private String itemName;
	private final Class<T> itemClass;
	private Function<List<T>, List<AbstractColumn<T, ? extends Object>>> columnCreator;
	private final ListChangeListener<T> itemListChangeListener;
	private boolean editable = true;
	private boolean filterable = true;

	// FilterTable
	private final ObservableList<T> filteredItems;
	private Predicate<T> filterPredicate;
	private List<Predicate<T>> columnItemFilterPredicates;
	private List<Predicate<T>> columnTextFilterPredicates;
	private Comparator<T> sortComparator;
	private int nFilteredCols;
	private List<Integer> mappedColumnIndexes;
	private Function<Integer, Integer> columnIndexMapper;
	private Queue<Comparator<T>> sortComparatorQueue;
	

	{
		// TableBase
		items = FXCollections.observableArrayList();
		columns = new ArrayList<AbstractColumn<T, ? extends Object>>();
		columnCreator = items -> new ArrayList<AbstractColumn<T, ? extends Object>>();

		itemListChangeListener = new ListChangeListener<T>() {
			@Override
			public void onChanged(Change<? extends T> c) {
//				updateColumns();
			}
		};
		items.addListener(itemListChangeListener);

		// FilterTable
		filteredItems = FXCollections.observableArrayList();
		filterPredicate = item -> true;
		columnItemFilterPredicates = new ArrayList<>();
		columnTextFilterPredicates = new ArrayList<>();
		sortComparator = (i1, i2) -> {
			if (i1 instanceof Comparable) return ((Comparable<T>) i1).compareTo(i2);
			return 0;
		};
		mappedColumnIndexes = new ArrayList<>();
		sortComparatorQueue = new LimitedQueue<>(1);

		TableBase<T> reference = this;
		ObservableList<T> unfilteredItems = getItems();

		ListChangeListener<T> unfilteredListener = reference.getItemListChangeListener();
		ListChangeListener<T> filteredListener = (ListChangeListener<T>) c -> {
			unfilteredItems.removeListener(unfilteredListener);
			while (c.next()) {
				if (c.wasAdded())
					CollectionUtil.addIfAbsent(c.getAddedSubList(), unfilteredItems);
			}
			unfilteredItems.addListener(unfilteredListener);
		};
		filteredItems.addListener(filteredListener);
	}
	
	public TableBase(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.itemName = itemClass.getSimpleName();
	}

	public void refresh() {
		Logger.debug.println("TableBase - refresh");
		updateColumns();

		if (isFilterable()) {
			CollectionUtil.addIfAbsent(filteredItems, getItems());
			CollectionUtil.removeIfAbsent(getItems(), filteredItems);

			applyFilter();
		}
	}

	public void updateColumns() {
		Logger.debug.println("TableBase - updateColumns");
		CollectionUtil.syncLists(this.columns, columnCreator.apply(getItems()),
			(column, newColumn) -> column.getName().equals(newColumn.getName()));
		getColumns().stream().forEach(column -> {
			column.setItems(getItems());
		});

		if (isFilterable()) {
			int nCols = getNcols();
			mappedColumnIndexes = new ArrayList<Integer>(nCols);
			for (int i = 0; i < nCols; i++)
				if (getColumns().get(i).isVisible()) mappedColumnIndexes.add(i);
			nFilteredCols = mappedColumnIndexes.size();
			for (int i = 0; i < nCols; i++) {
				columnItemFilterPredicates.add(item -> true);
				columnTextFilterPredicates.add(item -> true);
			}
			columnIndexMapper = i -> mappedColumnIndexes.get(i);
			
			getFilteredColumns().stream()
				.collect(castFilter(column -> ((AbstractFilterColumn<T, ?>) column)))
				.forEach(column -> column.setFilteredItems(getFilteredItems()));

			applyFilter();
		}
	}

	public void applyFilter() {
		Logger.debug.println("TableBase - applyFilter");
		filterPredicate = mappedColumnIndexes.stream()
			.filter(i -> i < columnItemFilterPredicates.size())
			.map(i -> getColumns().get(i))
			.collect(castFilter(column -> ((FilterColumn<T, ?, ?>) column)))
			.map(filterColumn -> filterColumn.getFilterPredicate())
			.reduce((f1, f2) -> f1.and(f2)).orElse(item -> true);

		sortComparator = sortComparatorQueue.stream()
			.reduce((c1, c2) -> c2.thenComparing(c1)).orElse((c1, c2) -> 0);

		this.filteredItems.clear();
		this.filteredItems.addAll(getItems().stream().filter(filterPredicate)
			.sorted(sortComparator).collect(Collectors.toList()));
//
//		System.out.println(toString());
	}

	public void setColumnItemFilterValues(int columnIndex, List<Object> values) {
		columnItemFilterPredicates.set(columnIndexMapper.apply(columnIndex), item -> {
			return values.stream()
				.filter(value -> getFilteredValue(item, columnIndex).equals(value))
				.findFirst().isPresent();
		});
	}

	public void setColumnTextFilterValues(int columnIndex, String searchText) {
		final Pattern p = Pattern.compile("(?i)" + searchText);
		if (searchText.isEmpty()) columnTextFilterPredicates
			.set(columnIndexMapper.apply(columnIndex), item -> true);
		else columnTextFilterPredicates.set(columnIndexMapper.apply(columnIndex),
			item -> p.matcher(getFilteredValue(item, columnIndex).toString()).find());
	}

	public void setColumnCreator(
		Function<List<T>, List<AbstractColumn<T, ?>>> columnCreator) {
		this.columnCreator = columnCreator;
		updateColumns();
	}

	public Object getColumnFilteredValue(int row, int col) {
		List<AbstractColumn<T, ? extends Object>> columns = getFilteredColumns();
		AbstractColumn<T, ?> column = columns.get(col);
		ObservableList<T> items = getItems();
		T item = items.get(row);
		column.get(item);
		return getFilteredColumns().get(col).get(getItems().get(row));
	}

	public Object getFilteredValue(int row, int col) {
		return getFilteredColumns().get(col).get(getFilteredItems().get(row));
	}

	public Object getFilteredValue(T item, int col) {
		return getValue(item, columnIndexMapper.apply(col));
	}

	public Object getValue(int row, int col) {
		return getValue(getItems().get(row), col);
	}

	public Object getValue(T item, int col) {
		return getColumns().get(col).get(item);
	}

	public void setValue(T item, int col, Object value) {
		getColumns().get(col).set(item, value);
	}

	public void setValue(int row, int col, Object value) {
		setValue(getItems().get(row), col, value);
	}

	public void setFilteredValue(int row, int col, Object value) {
		if (isEditable()) setFilteredValue(getFilteredItems().get(row), col, value);
	}

	public void setFilteredValue(T filteredItem, int filteredCol, Object value) {
		if (isEditable()) getFilteredColumns().get(filteredCol).set(filteredItem, value);
	}

	public List<String> getFilteredColumnNames() {
		return getFilteredColumns().stream().map(c -> c.getName())
			.collect(Collectors.toList());
	}

	public List<AbstractFilterColumn<T, ? extends Object>> getFilteredFilterColumns() {
		return getFilteredColumns().stream()
			.collect(castFilter(column -> (AbstractFilterColumn<T, ?>) column))
			.collect(Collectors.toList());
	}

	public List<AbstractColumn<T, ? extends Object>> getFilteredColumns() {
		return mappedColumnIndexes.stream().map(i -> getColumns().get(i))
			.collect(Collectors.toList());
	}

	public List<String> getColumnNames() {
		return getColumns().stream().map(c -> c.getName()).collect(Collectors.toList());
	}

	public int getNrows() {
		return getItems().size();
	}

	public int getNcols() {
		return getColumns().size();
	}

	@Override
	public String toString() {
		if (!isFilterable()) return createString(getNrows(), getNcols(), getColumnNames(),
			(row, col) -> getValue(row, col).toString());
		else return createString(getFilteredNrows(), getFilteredNcols(),
			getFilteredColumnNames(),
			(row, col) -> getFilteredValue(row, col).toString());
	}

	private String createString(int nRows, int nCols, List<String> columnNames,
		BiFunction<Integer, Integer, String> valueGetter) {
		if (nRows == 0 || nCols == 0) return "";
		String[][] content = new String[nRows + 1][nCols];

		// filling content with values
		for (int col = 0; col < nCols; col++)
			content[0][col] = columnNames.get(col);
		for (int row = 0; row < nRows; row++)
			for (int col = 0; col < nCols; col++)
				content[row + 1][col] = valueGetter.apply(row, col);

		// determine column width
		int[] columnWidth = new int[nCols];
		for (int col = 0; col < nCols; col++) {
			int maxLength = content[0][col].length();
			for (int row = 0; row < nRows; row++) {
				if (content[row + 1][col].length() > maxLength)
					maxLength = content[row + 1][col].length();
			}
			columnWidth[col] = maxLength;
		}

		// enlarge values to column width
		List<String> columnStrings = new ArrayList<String>();

		for (int col = 0; col < nCols; col++) {
			content[0][col] = content[0][col]
				+ StringUtils.repeat(" ", columnWidth[col] - content[0][col].length());
		}
		columnStrings.add(Arrays.stream(content[0]).collect(Collectors.joining("|")));
		columnStrings.add(StringUtils.repeat("-", columnStrings.get(0).length()));
		for (int row = 1; row < nRows + 1; row++) {
			for (int col = 0; col < nCols; col++)
				content[row][col] = content[row][col] + StringUtils.repeat(" ",
					columnWidth[col] - content[row][col].length());
			columnStrings
				.add(Arrays.stream(content[row]).collect(Collectors.joining("|")));
		}

		return columnStrings.stream().collect(Collectors.joining("\n"));
	}

	// Getters & Setters
	public Function<List<T>, List<AbstractColumn<T, ? extends Object>>>
		getColumnCreator() {
		return columnCreator;
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public ListChangeListener<T> getItemListChangeListener() {
		return itemListChangeListener;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String name) {
		this.tableName = name;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String name) {
		this.itemName = name;
	}

	public List<AbstractColumn<T, ? extends Object>> getColumns() {
		return this.columns;
	}

	public ObservableList<T> getItems() {
		return this.items;
	}

	public final Class<T> getItemClass() {
		return this.itemClass;
	}

	// Getters & Setters Filter
	public Queue<Comparator<T>> getSortComparatorQueue() {
		return sortComparatorQueue;
	}

	public void setSortComparator(Comparator<T> sortComparator) {
		this.sortComparator = sortComparator;
	}

	public void setSortComparatorQueue(Queue<Comparator<T>> sortComparatorQueue) {
		this.sortComparatorQueue = sortComparatorQueue;
	}

	public ObservableList<T> getFilteredItems() {
		return filteredItems;
	}

	public int getFilteredNrows() {
		return filteredItems.size();
	}

	public int getFilteredNcols() {
		return nFilteredCols;
	}

	public Predicate<T> getFilterPredicate() {
		return filterPredicate;
	}

}
