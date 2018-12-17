package org.artorg.tools.phantomData.client.column;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.model.DbPersistent;

public class Column<T, S,R> extends AbstractColumn<T,R> {
	private final Function<T, S> itemToPropertyGetter;
	private final Function<S, R> propertyToValueGetter;
	private final BiConsumer<S, R> propertyToValueSetter;
	
	@SuppressWarnings("unchecked")
	public Column(String columnName, Function<T, R> propertyToValueGetter) {
		this(columnName, item -> (S) item, (S sub) -> propertyToValueGetter.apply((T) sub), (sub, value) -> {});
	}
	
	@SuppressWarnings("unchecked")
	public Column(String columnName, Function<T, R> propertyToValueGetter,
			BiConsumer<T, R> propertyToValueSetter) {
		this(columnName, item -> (S) item, (S sub) -> propertyToValueGetter.apply((T) sub),
				(S sub, R value) -> propertyToValueSetter.accept((T) sub, value));
	}

	public Column(String columnName, Function<T, S> itemToPropertyGetter,
			Function<S, R> propertyToValueGetter) {
		this(columnName, itemToPropertyGetter, propertyToValueGetter, (sub, r) -> {});
	}
	
	public Column(String columnName, Function<T, S> itemToPropertyGetter, 
			Function<S, R> propertyToValueGetter, 
			BiConsumer<S, R> propertyToValueSetter) {
		super(columnName);
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
	}
	
	@Override
	public R get(T item) {
		S o = itemToPropertyGetter.apply(item);
		if (o == null) return null;
		return propertyToValueGetter.apply(o);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void set(T item, Object value) {
		S o = itemToPropertyGetter.apply(item);
		if (o == null) return;
		propertyToValueSetter.accept(o, (R)value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean update(T item) {
		S path = itemToPropertyGetter.apply(item);
		ICrudConnector<S> connector = Connectors.getConnector(((DbPersistent<S,?>)path).getItemClass());
		return connector.update(path);
	}
	
}
