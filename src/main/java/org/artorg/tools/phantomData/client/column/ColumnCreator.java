package org.artorg.tools.phantomData.client.column;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ColumnCreator<T, S> {
	private final Class<T> itemClass;
	private final Function<T, S> itemToPropertyGetter;

	@SuppressWarnings("unchecked")
	public ColumnCreator(Class<T> itemClass) {
		this.itemClass = itemClass;
		this.itemToPropertyGetter = item -> (S)item;
	}
	
	public ColumnCreator(Class<T> itemClass, Function<T, S> itemToPropertyGetter) {
		this.itemClass = itemClass;
		this.itemToPropertyGetter = itemToPropertyGetter;
	}

	// Column
//	public <R> Column<T, S, R> createColumn(String columnName,
//			Function<S, R> propertyToValueGetter) {
//		return new Column<T, S, R>(itemClass, columnName, itemToPropertyGetter,
//				propertyToValueGetter, (S s, R r) -> {});
//	}
//
//	public <R> Column<T, S, R> createColumn(String columnName, Function<S, R> propertyToValueGetter,
//			BiConsumer<S, R> propertyToValueSetter) {
//		return new Column<T, S, R>(itemClass, columnName, itemToPropertyGetter,
//				propertyToValueGetter, propertyToValueSetter);
//	}

	// FilterColumn
	public <R> FilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter) {
		return new FilterColumn<T, S, R>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, (S s, R r) -> {});
	}

	public <R> FilterColumn<T, S, R> createFilterColumn(String columnName,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter) {
		return new FilterColumn<T, S, R>(itemClass, columnName, itemToPropertyGetter,
				propertyToValueGetter, propertyToValueSetter);
	}
	
}
