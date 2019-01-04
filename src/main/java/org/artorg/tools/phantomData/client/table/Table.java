package org.artorg.tools.phantomData.client.table;

import static org.artorg.tools.phantomData.client.util.StreamUtils.castFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.logging.Logger;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.LimitedQueue;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@SuppressWarnings("unchecked")
public abstract class Table<T> {
	private final ObservableList<T> items;
	private List<AbstractColumn<T, ? extends Object>> columns;
	private String itemName;
	private final Class<T> itemClass;
	private final ListChangeListener<T> itemListChangeListener;
	private boolean editable = true;
	private boolean filterable = true;
	private String tableName;

	// FilterTable
	private final ObservableList<T> filteredItems;
	private final ObservableList<T> readOnlyFilteredItems;
	private Predicate<T> filterPredicate;
	private List<Predicate<T>> columnItemFilterPredicates;
	private List<Predicate<T>> columnTextFilterPredicates;
	private Comparator<T> sortComparator;
	private Queue<Comparator<T>> sortComparatorQueue;

	{
		// TableBase
		items = FXCollections.observableArrayList();
		columns = new ArrayList<AbstractColumn<T, ? extends Object>>();

		// FilterTable
		filteredItems = FXCollections.observableArrayList();
		readOnlyFilteredItems = FXCollections.unmodifiableObservableList(filteredItems);
		filterPredicate = item -> true;
		columnItemFilterPredicates = new ArrayList<>();
		columnTextFilterPredicates = new ArrayList<>();
		sortComparator = (i1, i2) -> {
			if (i1 instanceof Comparable) return ((Comparable<T>) i1).compareTo(i2);
			return 0;
		};
		sortComparatorQueue = new LimitedQueue<>(1);

		itemListChangeListener = c -> {
			while (c.next()) {
				CollectionUtil.addIfAbsent(items, filteredItems);
				CollectionUtil.removeIfAbsent(items, filteredItems);
				updateColumns();
			}
		};
		items.addListener(itemListChangeListener);
	}

	public Table(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.itemName = itemClass.getSimpleName();
	}

	public abstract List<AbstractColumn<T, ? extends Object>> createColumns(List<T> items);

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String name) {
		this.tableName = name;
	}

	public void refresh() {
		Logger.debug.println(getItemClass().getSimpleName());
		updateColumns();
		if (isFilterable()) applyFilter();
	}

	public void updateColumns() {
		long startTime = System.currentTimeMillis();
		List<AbstractColumn<T, ? extends Object>> columns = createColumns(getItems());

		List<Integer> indexes = CollectionUtil.searchLeftNotInRight(this.columns, columns,
				(col1, col2) -> col1.getName().equals(col2.getName()));
		List<AbstractColumn<T, ? extends Object>> removableColumns =
				CollectionUtil.subList(this.columns, indexes);
		this.columns.removeAll(removableColumns);

		for (int i = 0; i < columns.size(); i++) {
			if (i >= this.columns.size()) addColumn(this.columns.size(), columns.get(i));
			if (!this.columns.get(i).getName().equals(columns.get(i).getName()))
				addColumn(i, columns.get(i));
		}

		if (isFilterable()) {
			int nCols = getNcols();
			for (int i = 0; i < nCols; i++) {
				columnItemFilterPredicates.add(item -> true);
				columnTextFilterPredicates.add(item -> true);
			}
		}
		Logger.debug.println(getItemClass().getSimpleName() + " - Updated " + getColumns().size()
				+ " column(s) in " + (System.currentTimeMillis() - startTime) + " ms");
	}

	private void addColumn(int index, AbstractColumn<T, ? extends Object> column) {
		if (column instanceof AbstractFilterColumn) {
			AbstractFilterColumn<T, ?> filterColumn = (AbstractFilterColumn<T, ?>) column;
			filterColumn.setSortComparatorQueue(getSortComparatorQueue());
		} else {
			Logger.debug.println("NON FILTERING COLUMN");
		}
		this.columns.add(index, column);
	}

	public void resetFilter() {
		filterPredicate = item -> true;
		getFilteredItems().clear();
		getFilteredItems().addAll(getItems());
	}

	private boolean filterActivated = true;

	public boolean isFilterActivated() {
		return filterActivated;
	}

	public void setFilterActivated(boolean filterActivated) {
		this.filterActivated = filterActivated;
	}

	public void applyFilter() {
		if (!filterActivated) {
			resetFilter();
			return;
		}
		filterPredicate = getColumns().stream()
				.collect(castFilter(column -> ((AbstractFilterColumn<T, ?>) column)))
				.map(filterColumn -> filterColumn.getFilterPredicate())
				.reduce((f1, f2) -> f1.and(f2)).orElse(item -> true);

		sortComparator = sortComparatorQueue.stream().reduce((c1, c2) -> c2.thenComparing(c1))
				.orElse((c1, c2) -> 0);

		List<T> newFilteredItems = getItems().stream().filter(filterPredicate)
				.sorted(sortComparator).collect(Collectors.toList());

		this.filteredItems.clear();
		this.filteredItems.addAll(newFilteredItems);

		Logger.debug.println(getItemClass().getSimpleName());
	}

	public void setColumnItemFilterValues(int columnIndex, List<Object> values) {
		columnItemFilterPredicates.set(columnIndex, item -> {
			return values.stream()
					.filter(value -> getFilteredValue(item, columnIndex).equals(value)).findFirst()
					.isPresent();
		});
	}

	public void setColumnTextFilterValues(int columnIndex, String searchText) {
		final Pattern p = Pattern.compile("(?i)" + searchText);
		if (searchText.isEmpty()) columnTextFilterPredicates.set(columnIndex, item -> true);
		else
			columnTextFilterPredicates.set(columnIndex,
					item -> p.matcher(getFilteredValue(item, columnIndex).toString()).find());
	}

	public Object getFilteredValue(int row, int col) {
		return getColumns().get(col).get(getFilteredItems().get(row));
	}

	public Object getFilteredValue(T item, int col) {
		return getValue(item, col);
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
		if (isEditable()) getColumns().get(filteredCol).set(filteredItem, value);
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
		else
			return createString(getFilteredNrows(), getNcols(), getColumnNames(),
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
			for (int col = 0; col < nCols; col++) {
				try {
					content[row + 1][col] = valueGetter.apply(row, col);
				} catch (NullPointerException e) {
					content[row + 1][col] = "null";
				}
			}

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
				content[row][col] = content[row][col]
						+ StringUtils.repeat(" ", columnWidth[col] - content[row][col].length());
			columnStrings.add(Arrays.stream(content[row]).collect(Collectors.joining("|")));
		}

		return columnStrings.stream().collect(Collectors.joining("\n"));
	}

	// Getters & Setters
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

	protected ListChangeListener<T> getItemListChangeListener() {
		return itemListChangeListener;
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
		return readOnlyFilteredItems;
	}

	public int getFilteredNrows() {
		return filteredItems.size();
	}

	public Predicate<T> getFilterPredicate() {
		return filterPredicate;
	}

}
