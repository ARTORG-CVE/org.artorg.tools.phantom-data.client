package org.artorg.tools.phantomData.client.column;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.table.Table;

public class ColumnCreator<T, S> {
	private final Table<T> table;
	private final Function<T, S> itemToPropertyGetter;

	@SuppressWarnings("unchecked")
	public ColumnCreator(Table<T> table) {
		this.table = table;
		this.itemToPropertyGetter = item -> (S)item;
	}
	
	public ColumnCreator(Table<T> table, Function<T, S> itemToPropertyGetter) {
		this.table = table;
		this.itemToPropertyGetter = itemToPropertyGetter;
	}

	// Column
	public <R> Column<T, S, R> createColumn(String columnName,
			Function<S, R> propertyToValueGetter) {
		return new Column<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, (S s, R r) -> {});
	}

	public <R> Column<T, S, R> createColumn(String columnName, Function<S, R> propertyToValueGetter,
			BiConsumer<S, R> propertyToValueSetter) {
		return new Column<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter);
	}

	// FilterColumn
	public <R> FilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter) {
		return new FilterColumn<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, (S s, R r) -> {});
	}

	public <R> FilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter) {
		return new FilterColumn<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter);
	}
	
}
