package org.artorg.tools.phantomData.client.table;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class FilterColumn<ITEM> extends Column<ITEM> {
	private Comparator<ITEM> sortComparator;
	private Predicate<ITEM> filterPredicate;
	
	

	{
		sortComparator = (i1,i2) -> i1.toString().compareTo(i2.toString());
	}

	public FilterColumn(String columnName) {
		super(columnName);
	}

	
	
	// Getters & Setters
	public Comparator<ITEM> getSortComparator() {
		return sortComparator;
	}

	public Predicate<ITEM> getFilterPredicate() {
		return filterPredicate;
	}

	public void setFilterPredicate(Predicate<ITEM> filterPredicate) {
		this.filterPredicate = filterPredicate;
	}
	
	public void setSortComparator(Comparator<ITEM> sortComparator) {
		this.sortComparator = sortComparator;
	}
	
	public void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator.compare(valueGetter.apply(item1),valueGetter.apply(item2));
	}
	
}
