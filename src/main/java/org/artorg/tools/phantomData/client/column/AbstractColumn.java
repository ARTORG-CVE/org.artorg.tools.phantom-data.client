package org.artorg.tools.phantomData.client.column;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.table.Table;

public abstract class AbstractColumn<T, E> {
	private final Table<T> table;
	private boolean visible;
	private boolean editable;
	private boolean filterable;
	private boolean idColumn;
	private final String columnName;
	private Function<T,E> valueGetter;
	private BiConsumer<T,E> valueSetter;
	private Class<T> itemClass;

	{
		visible = true;
		editable = true;
		filterable = true;
		idColumn = false;
	}
	
	public AbstractColumn(Table<T> table, String columnName) {
		this.table = table;
		this.itemClass = table.getItemClass();
		this.columnName = columnName;
	}
	
	public abstract boolean update(T item);
	
	public List<E> getValues() {
		return table.getItems().stream().map(item -> get(item)).collect(Collectors.toList());
	}
	
	public E get(T item) {
		return valueGetter.apply(item);
	}
	
	public boolean contains(T item) {
		try {
			valueGetter.apply(item);
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	public boolean contains(String value) {
		return table.getItems().stream().filter(item -> get(item).equals(value))
				.findFirst().isPresent();
	}
	
	@SuppressWarnings("unchecked")
	public void set(T item, Object value) {
		valueSetter.accept(item, (E)value);
	}
	
	// Getters & Setters
	public Class<T> getItemClass() {
		return itemClass;
	}

	public void setItemClass(Class<T> itemClass) {
		this.itemClass = itemClass;
	}
	
	public Function<T, E> getValueGetter() {
		return valueGetter;
	}
	
	public void setValueGetter(Function<T, E> valueGetter) {
		this.valueGetter = valueGetter;
	}

	public BiConsumer<T, E> getValueSetter() {
		return valueSetter;
	}

	public void setValueSetter(BiConsumer<T, E> valueSetter) {
		this.valueSetter = valueSetter;
	}
	
	public String getName() {
		return columnName;
	}
	
	public Table<T> getTable() {
		return table;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}
	
	public boolean isIdColumn() {
		return idColumn;
	}
	
	public void setIdColumn(boolean idColumn) {
		this.idColumn = idColumn;
	}

}
