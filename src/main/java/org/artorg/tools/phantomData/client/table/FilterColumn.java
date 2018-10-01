package org.artorg.tools.phantomData.client.table;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FilterColumn<ITEM> extends AbstractColumn<ITEM> {
	private Comparator<ITEM> sortComparator;
	private Comparator<ITEM> ascendingSortComparator;
	private Predicate<ITEM> filterPredicate;
	private Queue<Comparator<ITEM>> sortComparatorQueue;
	private final Function<ITEM, Object> itemToPropertyGetter;
	private final Function<Object, String> propertyToValueGetter;
	private final BiConsumer<Object, String> propertyToValueSetter;
	private ObservableList<ITEM> filteredItems;

	public ObservableList<ITEM> getFilteredItems() {
		return filteredItems;
	}

	public void setFilteredItems(ObservableList<ITEM> filteredItems) {
		this.filteredItems = filteredItems;
	}

	{
		filteredItems = FXCollections.observableArrayList();
		resetFilter();
	}

	@SuppressWarnings("unchecked")
	public <SUB extends DbPersistent<SUB,?>> FilterColumn(String columnName, Function<ITEM, SUB> itemToPropertyGetter, 
			Function<SUB, String> propertyToValueGetter, 
			BiConsumer<SUB, String> propertyToValueSetter) {
		super(columnName);
		this.itemToPropertyGetter = (Function<ITEM, Object>) itemToPropertyGetter;
		this.propertyToValueGetter = (Function<Object, String>) propertyToValueGetter;
		this.propertyToValueSetter = (BiConsumer<Object, String>) propertyToValueSetter;
	}

	
	public List<String> getFilteredValues() {
		return getFilteredItems().stream().map(item -> get(item)).collect(Collectors.toList());
	}
	
	
	@Override
	public String get(ITEM item) {
		return propertyToValueGetter.apply(itemToPropertyGetter.apply(item));
	}

	@Override
	public void set(ITEM item, String value) {
		propertyToValueSetter.accept(itemToPropertyGetter.apply(item), value);
	}

	@SuppressWarnings("unchecked")
	public <U extends DbPersistent<U, SUB_ID>, SUB_ID> boolean update(ITEM item) {
		System.out.println("updated value in database :)");
		U path = (U) itemToPropertyGetter.apply(item);
		ICrudConnector<U, SUB_ID> connector = Connectors.getConnector(path.getItemClass());
		return connector.update(path);
	}
	
	public void resetFilter() {
		ascendingSortComparator = (i1,i2) -> get(i1).compareTo(get(i2));
		filterPredicate = item -> true;
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
	
	public void setSortComparator(Comparator<ITEM> sortComparator) {
		this.sortComparator = sortComparator;
		sortComparatorQueue.add(sortComparator);
	}
	
	public void setSortComparator(Comparator<String> sortComparator, Function<ITEM, String> valueGetter) {
		this.sortComparator = (item1, item2) -> sortComparator.compare(valueGetter.apply(item1),valueGetter.apply(item2));
	}

	public Queue<Comparator<ITEM>> getSortComparatorQueue() {
		return sortComparatorQueue;
	}
	
	public void setSortComparatorQueue(Queue<Comparator<ITEM>> sortComparatorQueue) {
		this.sortComparatorQueue = sortComparatorQueue;
	}
	
}
