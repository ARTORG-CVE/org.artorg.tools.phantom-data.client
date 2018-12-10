package org.artorg.tools.phantomData.client.table.column;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class AbstractFilterColumn<ITEM,R> extends AbstractColumn<ITEM, R> {
	private Comparator<ITEM> sortComparator;
	private Comparator<ITEM> ascendingSortComparator;
	private Predicate<ITEM> filterPredicate;
	private Queue<Comparator<ITEM>> sortComparatorQueue;
	private ObservableList<ITEM> filteredItems;

	{
		filteredItems = FXCollections.observableArrayList();
		resetFilter();
	}

	public AbstractFilterColumn(String columnName) {
		super(columnName);
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
	
	public void setSortComparator(Comparator<ITEM> sortComparator) {
		this.sortComparator = sortComparator;
		sortComparatorQueue.add(sortComparator);
	}

	public void setSortComparator(Comparator<R> sortComparator,
		Function<ITEM, R> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator
			.compare(valueGetter.apply(item1), valueGetter.apply(item2));
	}

	// Getters & Setters
	public Comparator<ITEM> getAscendingSortComparator() {
		return ascendingSortComparator;
	}

	public void setAscendingSortComparator(Comparator<ITEM> ascendingComparator) {
		this.ascendingSortComparator = ascendingComparator;
	}

	public Comparator<ITEM> getSortComparator() {
		return sortComparator;
	}

	public Predicate<ITEM> getFilterPredicate() {
		return filterPredicate;
	}

	public void setFilterPredicate(Predicate<ITEM> filterPredicate) {
		this.filterPredicate = filterPredicate;
	}

	public Queue<Comparator<ITEM>> getSortComparatorQueue() {
		return sortComparatorQueue;
	}

	public void setSortComparatorQueue(Queue<Comparator<ITEM>> sortComparatorQueue) {
		this.sortComparatorQueue = sortComparatorQueue;
	}
	
	public ObservableList<ITEM> getFilteredItems() {
		return filteredItems;
	}

	public void setFilteredItems(ObservableList<ITEM> filteredItems) {
		this.filteredItems = filteredItems;
	}

}
