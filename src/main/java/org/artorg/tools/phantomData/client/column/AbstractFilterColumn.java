package org.artorg.tools.phantomData.client.column;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class AbstractFilterColumn<T,R> extends AbstractColumn<T, R> {
	private Comparator<T> sortComparator;
	private Comparator<T> ascendingSortComparator;
	private Predicate<T> filterPredicate;
	private Queue<Comparator<T>> sortComparatorQueue;
	private ObservableList<T> filteredItems;
	private int maxFilterItems;
	private boolean itemsFilter;

	{
		filteredItems = FXCollections.observableArrayList();
		resetFilter();
		maxFilterItems = 10;
		itemsFilter = true;
	}

	public AbstractFilterColumn(Class<T> itemClass, String columnName) {
		super(itemClass, columnName);
	}

	@SuppressWarnings("unchecked")
	public void resetFilter() {
		ascendingSortComparator = (i1, i2) -> {
			if (i1 instanceof Comparable && i2 instanceof Comparable)
			return ((Comparable<R>)get(i1)).compareTo(get(i2));
			return 0;
			};
		filterPredicate = item -> true;
	}
	
	public List<R> getFilteredValues() {
		return getFilteredItems().stream().map(item -> get(item))
			.collect(Collectors.toList());
	}
	
	public void setSortComparator(Comparator<T> sortComparator) {
		this.sortComparator = sortComparator;
		sortComparatorQueue.add(sortComparator);
	}

	public void setSortComparator(Comparator<R> sortComparator,
		Function<T, R> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator
			.compare(valueGetter.apply(item1), valueGetter.apply(item2));
	}

	// Getters & Setters
	public Comparator<T> getAscendingSortComparator() {
		return ascendingSortComparator;
	}

	public void setAscendingSortComparator(Comparator<T> ascendingComparator) {
		this.ascendingSortComparator = ascendingComparator;
	}

	public Comparator<T> getSortComparator() {
		return sortComparator;
	}

	public Predicate<T> getFilterPredicate() {
		return filterPredicate;
	}

	public void setFilterPredicate(Predicate<T> filterPredicate) {
		this.filterPredicate = filterPredicate;
	}

	public Queue<Comparator<T>> getSortComparatorQueue() {
		return sortComparatorQueue;
	}

	public void setSortComparatorQueue(Queue<Comparator<T>> sortComparatorQueue) {
		this.sortComparatorQueue = sortComparatorQueue;
	}
	
	public ObservableList<T> getFilteredItems() {
		return filteredItems;
	}

	public void setFilteredItems(ObservableList<T> filteredItems) {
		this.filteredItems = filteredItems;
	}

	public int getMaxFilterItems() {
		return maxFilterItems;
	}

	public void setMaxFilterItems(int maxFilterItems) {
		this.maxFilterItems = maxFilterItems;
	}

	public boolean isItemsFilter() {
		return itemsFilter;
	}

	public void setItemsFilter(boolean itemsFilter) {
		this.itemsFilter = itemsFilter;
	}
	
	
	
	

}
