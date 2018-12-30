package org.artorg.tools.phantomData.client.column;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PutException;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.model.DbPersistent;

public class Column<T, S, R> extends AbstractColumn<T, R> {
	private final Function<T, S> itemToPropertyGetter;
	private final Function<S, R> propertyToValueGetter;
	private final BiConsumer<S, R> propertyToValueSetter;
	
	public Column(Table<T> table, String columnName, Function<T, S> itemToPropertyGetter,
			Function<S, R> propertyToValueGetter, BiConsumer<S, R> propertyToValueSetter) {
		super(table, columnName);
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
		propertyToValueSetter.accept(o, (R) value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean update(T item) throws NoUserLoggedInException, PutException {
		S path = itemToPropertyGetter.apply(item);
		ICrudConnector<S> connector =
				Connectors.get(((DbPersistent<S, ?>) path).getItemClass());
		return connector.update(path);
	}

}
