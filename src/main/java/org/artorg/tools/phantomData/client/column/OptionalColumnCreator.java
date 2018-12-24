package org.artorg.tools.phantomData.client.column;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.table.Table;

public class OptionalColumnCreator<T, S> {
	private final Table<T> table;
	private final Function<T, Optional<S>> itemToPropertyGetter;

	public OptionalColumnCreator(Table<T> table,
			Function<T, Optional<S>> itemToPropertyGetter) {
		this.table = table;
		this.itemToPropertyGetter = itemToPropertyGetter;
	}

	// OptionalColumn
	public OptionalColumn<T, S, String> createColumn(String columnName,
			Function<S, String> propertyToValueGetter) {
		return new OptionalColumn<T, S, String>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, "");
	}

	public OptionalColumn<T, S, String> createColumn(String columnName,
			Function<S, String> propertyToValueGetter,
			BiConsumer<S, String> propertyToValueSetter) {
		return new OptionalColumn<T, S, String>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, "");
	}

	public <R> OptionalColumn<T, S, R> createColumn(String columnName,
			Function<S, R> propertyToValueGetter, R emptyValue) {
		return new OptionalColumn<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, emptyValue);
	}

	public <R> OptionalColumn<T, S, R> createColumn(String columnName,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter,
			R emptyValue) {
		return new OptionalColumn<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, emptyValue);
	}

	// OptionalFilterColumn
	public OptionalFilterColumn<T, S, String> createFilterColumn(String columnName,
			Function<S, String> propertyToValueGetter) {
		return new OptionalFilterColumn<T, S, String>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, "");
	}

	public OptionalFilterColumn<T, S, String> createFilterColumn(String columnName,
			Function<S, String> propertyToValueGetter,
			BiConsumer<S, String> propertyToValueSetter) {
		return new OptionalFilterColumn<T, S, String>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, "");
	}

	public <R> OptionalFilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter, R emptyValue) {
		return new OptionalFilterColumn<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, emptyValue);
	}

	public <R> OptionalFilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter,
			R emptyValue) {
		return new OptionalFilterColumn<T, S, R>(table, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, emptyValue);
	}

}
