package org.artorg.tools.phantomData.client.column;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class OptionalColumn<T, S, R extends Comparable<R>> extends AbstractColumn<T, R> {
	private final Function<T, Optional<? extends S>> itemToPropertyGetter;
	private final Function<S, R> propertyToValueGetter;
	private final BiConsumer<S, R> propertyToValueSetter;
	private final R emptyValue;

	@SuppressWarnings("unchecked")
	public OptionalColumn(String columnName, Function<T, String> propertyToValueGetter) {
		this(columnName, item -> (Optional<S>) item,
				(S sub) -> (R) propertyToValueGetter.apply((T) sub), (sub, value) -> {}, (R) "");
	}

	@SuppressWarnings("unchecked")
	public OptionalColumn(String columnName, Function<T, String> propertyToValueGetter,
			BiConsumer<T, R> propertyToValueSetter) {
		this(columnName, item -> (Optional<S>) item,
				(S sub) -> (R) propertyToValueGetter.apply((T) sub),
				(S sub, R value) -> propertyToValueSetter.accept((T) sub, value), (R) "");
	}

	@SuppressWarnings("unchecked")
	public OptionalColumn(String columnName, Function<T, Optional<S>> itemToPropertyGetter,
			Function<S, String> propertyToValueGetter) {
		this(columnName, itemToPropertyGetter, sub -> (R) propertyToValueGetter.apply(sub),
				(sub, r) -> {}, (R) "");
	}
	
	@SuppressWarnings("unchecked")
	public OptionalColumn(String columnName, Function<T, R> propertyToValueGetter, R emptyValue) {
		this(columnName, item -> (Optional<S>) item, (S sub) -> propertyToValueGetter.apply((T) sub), (sub, value) -> {}, emptyValue);
	}
	
	@SuppressWarnings("unchecked")
	public OptionalColumn(String columnName, Function<T, R> propertyToValueGetter,
			BiConsumer<T, R> propertyToValueSetter, R emptyValue) {
		this(columnName, item -> (Optional<S>) item, (S sub) -> propertyToValueGetter.apply((T) sub),
				(S sub, R value) -> propertyToValueSetter.accept((T) sub, value), emptyValue);
	}

	public OptionalColumn(String columnName, Function<T, Optional<S>> itemToPropertyGetter,
			Function<S, R> propertyToValueGetter, R emptyValue) {
		this(columnName, itemToPropertyGetter, propertyToValueGetter, (sub, r) -> {}, emptyValue);
	}
	
	public OptionalColumn(String columnName, Function<T, Optional<S>> itemToPropertyGetter,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter,
			R emptyValue) {
		super(columnName);
		this.itemToPropertyGetter = item -> itemToPropertyGetter.apply(item);
		this.propertyToValueGetter = sub -> propertyToValueGetter.apply((S) sub);
		this.propertyToValueSetter =
				(sub, value) -> propertyToValueSetter.accept(((S) sub), value);
		this.emptyValue = emptyValue;
	}

	@Override
	public R get(T item) {
		Optional<? extends S> optional = itemToPropertyGetter.apply(item);
		if (optional.isPresent()) {
			S sub = optional.get();
			if (sub == null) return emptyValue;
			return propertyToValueGetter.apply(sub);
		}
		return emptyValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(T item, Object value) {
		Optional<? extends S> optional = itemToPropertyGetter.apply(item);
		if (optional.isPresent()) {
			S sub = optional.get();
			if (sub == null) return;
			propertyToValueSetter.accept(sub, (R) value);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean update(T item) {
		Optional<S> optional = (Optional<S>) itemToPropertyGetter.apply(item);
		if (!optional.isPresent()) return false;
		S sub = optional.get();
		ICrudConnector<S> connector =
				Connectors.getConnector(((DbPersistent<S, ?>) sub).getItemClass());
		return connector.update(sub);
	}

}