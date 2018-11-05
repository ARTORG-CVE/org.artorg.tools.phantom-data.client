package org.artorg.tools.phantomData.client.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.artorg.tools.phantomData.client.util.CollectionUtil;
import org.artorg.tools.phantomData.client.util.LimitedQueue;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DbFilterTable<ITEM extends DbPersistent<ITEM, ?>> extends DbTable<ITEM> implements IFilterTable<ITEM> {
	private ObservableList<ITEM> filteredItems;
	private Predicate<ITEM> filterPredicate;
	private List<Predicate<ITEM>> columnItemFilterPredicates;
	private List<Predicate<ITEM>> columnTextFilterPredicates;
	private Comparator<ITEM> sortComparator;
	private int nFilteredCols;
	private List<Integer> mappedColumnIndexes;
	private Function<Integer, Integer> columnIndexMapper;
	private Queue<Comparator<ITEM>> sortComparatorQueue;

	{
		filteredItems = FXCollections.observableArrayList();
		filterPredicate = item -> true;
		columnItemFilterPredicates = new ArrayList<Predicate<ITEM>>();
		columnTextFilterPredicates = new ArrayList<Predicate<ITEM>>();
		sortComparator = (i1, i2) -> initSortComparator(i1, i2);
		mappedColumnIndexes = new ArrayList<>();
		sortComparatorQueue = new LimitedQueue<Comparator<ITEM>>(1);

		DbFilterTable<ITEM> reference = this;
		ObservableList<ITEM> unfilteredItems = super.getItems();
		
		ListChangeListener<ITEM> unfilteredListener = reference.getItemListChangeListener();
		
		ListChangeListener<ITEM> filteredListener = new ListChangeListener<ITEM>() {
			@Override
			public void onChanged(Change<? extends ITEM> c) {
				unfilteredItems.removeListener(unfilteredListener);
				while (c.next()) {
					if (c.wasAdded())
						unfilteredItems.addAll(c.getAddedSubList());
				}
				unfilteredItems.addListener(unfilteredListener);
				
			}
		};
		filteredItems.addListener(filteredListener);

//		ListChangeListener<ITEM> unfilteredListener = new ListChangeListener<ITEM>() {
//			@Override
//			public void onChanged(Change<? extends ITEM> c) {
//				System.out.println(
//						"" + new Date() + ", BASE:   list has changed!, " + reference.getClass().getSimpleName());
//			}
//		};
//		super.getItems().addListener(unfilteredListener);

	}

	@Override
	public Queue<Comparator<ITEM>> getSortComparatorQueue() {
		return sortComparatorQueue;
	}

	@Override
	public void setSortComparatorQueue(Queue<Comparator<ITEM>> sortComparatorQueue) {
		this.sortComparatorQueue = sortComparatorQueue;
	}

	private int initSortComparator(ITEM item1, ITEM item2) {
		if (item1 instanceof DbPersistent) {
			DbPersistent<ITEM, ?> dbPersistent1 = ((DbPersistent<ITEM, ?>) item1);
			DbPersistent<ITEM, ?> dbPersistent2 = ((DbPersistent<ITEM, ?>) item2);
			if (dbPersistent1.getId() instanceof Comparable)
				return (dbPersistent1.getId().toString()).compareTo(dbPersistent2.getId().toString());
		}
		return 0;
	}

	@Override
	public void refresh() {
		super.refresh();
		
//		filteredItems.clear();
//		filteredItems.addAll(super.getItems());

		CollectionUtil.addIfAbsent(filteredItems, super.getItems());
		CollectionUtil.removeIfAbsent(super.getItems(), filteredItems);
		
		
		
		getFilteredColumns().stream().forEach(column -> {
			column.setFilteredItems(getFilteredItems());
		});

		applyFilter();
		
	}
	
	@Override
	public void updateColumns() {
		super.updateColumns();

		int nCols = getNcols();

		mappedColumnIndexes = new ArrayList<Integer>(nCols);
		for (int i = 0; i < nCols; i++)
			if (getColumns().get(i).isVisible())
				mappedColumnIndexes.add(i);
		nFilteredCols = mappedColumnIndexes.size();
		for (int i = 0; i < nCols; i++) {
			columnItemFilterPredicates.add(item -> true);
			columnTextFilterPredicates.add(item -> true);
		}
		columnIndexMapper = i -> mappedColumnIndexes.get(i);

		getFilteredColumns().stream().forEach(column -> {
			column.setFilteredItems(getFilteredItems());
		});

		applyFilter();

	}

	@Override
	public void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator.compare(valueGetter.apply(item1),
				valueGetter.apply(item2));
	}

	@Override
	public ObservableList<ITEM> getFilteredItems() {
		return filteredItems;
	}

	@Override
	public void readAllData() {
		super.readAllData();
		
		CollectionUtil.syncLists(super.getItems(), filteredItems);
		
//		filteredItems.clear();
//		filteredItems.addAll(super.getItems());

		getFilteredColumns().stream().forEach(column -> {
			column.setFilteredItems(getFilteredItems());
		});

		applyFilter();
	}

	@Override
	public int getFilteredNrows() {
		return filteredItems.size();
	}

	@Override
	public int getFilteredNcols() {
		return nFilteredCols;
	}

	@Override
	public List<AbstractFilterColumn<ITEM>> getFilteredColumns() {
		return mappedColumnIndexes.stream().map(i -> (AbstractFilterColumn<ITEM>) getColumns().get(i))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getFilteredColumnNames() {
		return getFilteredColumns().stream().map(c -> c.getName()).collect(Collectors.toList());
	}

	@Override
	public String getFilteredValue(ITEM item, int col) {
		return getValue(item, columnIndexMapper.apply(col));
	}

	@Override
	public Predicate<ITEM> getFilterPredicate() {
		return filterPredicate;
	}

	@Override
	public void setColumnItemFilterValues(int columnIndex, List<String> values) {
		columnItemFilterPredicates.set(columnIndexMapper.apply(columnIndex), item -> {
			return values.stream().filter(value -> getFilteredValue(item, columnIndex).equals(value)).findFirst()
					.isPresent();
		});
	}

	@Override
	public void setColumnTextFilterValues(int columnIndex, String searchText) {
		final Pattern p = Pattern.compile("(?i)" + searchText);
		if (searchText.isEmpty())
			columnTextFilterPredicates.set(columnIndexMapper.apply(columnIndex), item -> true);
		else
			columnTextFilterPredicates.set(columnIndexMapper.apply(columnIndex),
					item -> p.matcher(getFilteredValue(item, columnIndex)).find());
	}

	@Override
	public void applyFilter() {
		filterPredicate = mappedColumnIndexes.stream().filter(i -> i < columnItemFilterPredicates.size())
				.map(i -> getColumns().get(i)).filter(column -> column instanceof FilterColumn)
				.map(column -> ((FilterColumn<ITEM>) column)).map(filterColumn -> filterColumn.getFilterPredicate())
				.reduce((f1, f2) -> f1.and(f2)).orElse(item -> true);

		sortComparator = sortComparatorQueue.stream().reduce((c1, c2) -> c2.thenComparing(c1)).orElse((c1, c2) -> 0);

		
		CollectionUtil.syncLists(super.getItems().stream().filter(filterPredicate).sorted(sortComparator).collect(Collectors.toList()), filteredItems);
		
		
//		this.filteredItems.clear();
//		this.filteredItems.addAll(
//				);

	}

	@Override
	public String toString() {
		int nRows = this.getFilteredNrows();
		int nCols = this.getFilteredNcols();
		if (nRows == 0 || nCols == 0)
			return "";
		String[][] content = new String[nRows + 1][nCols];

		for (int col = 0; col < nCols; col++)
			content[0][col] = getFilteredColumnNames().get(col);

		for (int row = 0; row < nRows; row++)
			for (int col = 0; col < nCols; col++)
				content[row + 1][col] = this.getFilteredValue(row, col);

		int[] columnWidth = new int[nCols];
		for (int col = 0; col < nCols; col++) {
			int maxLength = 0;
			for (int row = 0; row < nRows; row++) {
				if (content[row][col].length() > maxLength)
					maxLength = content[row][col].length();
			}
			columnWidth[col] = maxLength;
		}

		List<String> columnStrings = new ArrayList<String>();

		for (int col = 0; col < nCols; col++) {
			content[0][col] = content[0][col] + StringUtils.repeat(" ", columnWidth[col] - content[0][col].length());
		}
		columnStrings.add(Arrays.stream(content[0]).collect(Collectors.joining("|")));
		columnStrings.add(StringUtils.repeat("-", columnStrings.get(0).length()));

		for (int row = 0; row < nRows; row++) {
			for (int j = 0; j < nCols; j++)
				content[row + 1][j] = content[row + 1][j]
						+ StringUtils.repeat(" ", columnWidth[j] - content[row + 1][j].length());
			columnStrings.add(Arrays.stream(content[row + 1]).collect(Collectors.joining("|")));
		}

		return columnStrings.stream().collect(Collectors.joining("\n"));
	}

}
