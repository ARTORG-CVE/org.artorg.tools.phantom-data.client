package org.artorg.tools.phantomData.client.column;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.model.DbPersistent;

public class FilterColumn<T, S, R> extends AbstractFilterColumn<T, R> {
	private final Function<T, S> itemToPropertyGetter;
	private final Function<S, R> propertyToValueGetter;
	private final BiConsumer<S, R> propertyToValueSetter;
	
	public FilterColumn(Table<T> table, String columnName, Function<T, S> itemToPropertyGetter,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter) {
		super(table, columnName);
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
	}

	@Override
	public R get(T item) {
		S sub = itemToPropertyGetter.apply(item);
		if (sub == null) return null;
		R r = null;
		try {
			r = propertyToValueGetter.apply(sub);
		} catch (NullPointerException e) {}
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(T item, Object value) {
		S sub = itemToPropertyGetter.apply(item);
		if (sub == null) return;
		propertyToValueSetter.accept(sub, (R) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean update(T item) {
		S sub = itemToPropertyGetter.apply(item);
		ICrudConnector<S> connector =
				Connectors.get(((DbPersistent<S, ?>) sub).getItemClass());
		return connector.update(sub);
	}

}
