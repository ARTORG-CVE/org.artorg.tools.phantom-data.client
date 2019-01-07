package org.artorg.tools.phantomData.client.column;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.table.Table;

@SuppressWarnings("unchecked")
public abstract class AbstractFilterColumn<T, R> extends AbstractColumn<T, R> {
	private Comparator<T> ascendingSortComparator;
	private Predicate<T> filterPredicate;
	private int maxFilterItems;
	private boolean itemsFilter;

	{
		ascendingSortComparator = (i1, i2) -> {
			if (i1 instanceof Comparable)
				return ((Comparable<R>) get(i1)).compareTo(get(i2));
			return Integer.compare(get(i1).hashCode(),get(i2).hashCode());
		};
		filterPredicate = item -> true;
		maxFilterItems = 15;
		itemsFilter = true;
	}

	public AbstractFilterColumn(Table<T> table, String columnName) {
		super(table, columnName);
	}

	public List<R> getFilteredValues() {
		return getTable().getFilteredItems().stream().map(item -> get(item))
				.collect(Collectors.toList());
	}

	// Getters & Setters
	public Comparator<T> getAscendingSortComparator() {
		return ascendingSortComparator;
	}

	public void setAscendingSortComparator(Comparator<T> ascendingComparator) {
		this.ascendingSortComparator = ascendingComparator;
	}

	public Predicate<T> getFilterPredicate() {
		return filterPredicate;
	}

	public void setFilterPredicate(Predicate<T> filterPredicate) {
		this.filterPredicate = filterPredicate;
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
