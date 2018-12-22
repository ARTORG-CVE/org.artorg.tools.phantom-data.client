package org.artorg.tools.phantomData.client.column;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class OptionalColumnCreator<T, S> {
	private final Class<T> itemClass;
	private final Function<T, Optional<S>> itemToPropertyGetter;

	public OptionalColumnCreator(Class<T> itemClass,
			Function<T, Optional<S>> itemToPropertyGetter) {
		this.itemClass = itemClass;
		this.itemToPropertyGetter = itemToPropertyGetter;
	}

	// OptionalColumn
	public OptionalColumn<T, S, String> createColumn(String columnName,
			Function<S, String> propertyToValueGetter) {
		return new OptionalColumn<T, S, String>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, "");
	}

	public OptionalColumn<T, S, String> createColumn(String columnName,
			Function<S, String> propertyToValueGetter,
			BiConsumer<S, String> propertyToValueSetter) {
		return new OptionalColumn<T, S, String>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, "");
	}

	public <R> OptionalColumn<T, S, R> createColumn(String columnName,
			Function<S, R> propertyToValueGetter, R emptyValue) {
		return new OptionalColumn<T, S, R>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, emptyValue);
	}

	public <R> OptionalColumn<T, S, R> createColumn(String columnName,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter,
			R emptyValue) {
		return new OptionalColumn<T, S, R>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, emptyValue);
	}

	// OptionalFilterColumn
	public OptionalFilterColumn<T, S, String> createFilterColumn(String columnName,
			Function<S, String> propertyToValueGetter) {
		return new OptionalFilterColumn<T, S, String>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, "");
	}

	public OptionalFilterColumn<T, S, String> createFilterColumn(String columnName,
			Function<S, String> propertyToValueGetter,
			BiConsumer<S, String> propertyToValueSetter) {
		return new OptionalFilterColumn<T, S, String>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, "");
	}

	public <R> OptionalFilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter, R emptyValue) {
		return new OptionalFilterColumn<T, S, R>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, (sub, value) -> {}, emptyValue);
	}

	public <R> OptionalFilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter,
			R emptyValue) {
		return new OptionalFilterColumn<T, S, R>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter, emptyValue);
	}

}
