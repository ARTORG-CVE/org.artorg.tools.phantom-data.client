package org.artorg.tools.phantomData.client.column;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.model.DbPersistent;

public class OptionalFilterColumn<T, S, R> extends AbstractFilterColumn<T, R> {
	private final Function<T, Optional<? extends S>> itemToPropertyGetter;
	private final Function<S, R> propertyToValueGetter;
	private final BiConsumer<S, R> propertyToValueSetter;
	private final R emptyValue;

	public OptionalFilterColumn(Table<T> table, String columnName, Function<T, Optional<S>> itemToPropertyGetter,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter,
			R emtpyValue) {
		super(table, columnName);
		this.itemToPropertyGetter = item -> itemToPropertyGetter.apply(item);
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
		this.emptyValue = emtpyValue;
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
