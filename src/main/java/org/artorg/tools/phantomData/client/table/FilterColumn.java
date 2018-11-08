package org.artorg.tools.phantomData.client.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class FilterColumn<T,R> extends AbstractFilterColumn<T,R> {
	private final Function<T, Object> itemToPropertyGetter;
	private final Function<Object, R> propertyToValueGetter;
	private final BiConsumer<Object, R> propertyToValueSetter;

	@SuppressWarnings("unchecked")
	public <SUB extends DbPersistent<SUB,?>> FilterColumn(String columnName, Function<T, SUB> itemToPropertyGetter, 
			Function<SUB, R> propertyToValueGetter, 
			BiConsumer<SUB, R> propertyToValueSetter) {
		super(columnName);
		this.itemToPropertyGetter = (Function<T, Object>) itemToPropertyGetter;
		this.propertyToValueGetter = (Function<Object, R>) propertyToValueGetter;
		this.propertyToValueSetter = (BiConsumer<Object, R>) propertyToValueSetter;
	}
	
	@Override
	public R get(T item) {
		Object o = itemToPropertyGetter.apply(item);
		if (o == null) return null;
		return propertyToValueGetter.apply(o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(T item, Object value) {
		Object o = itemToPropertyGetter.apply(item);
		if (o == null) return;
		propertyToValueSetter.accept(o, (R)value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U extends DbPersistent<U, SUB_ID>, SUB_ID extends Comparable<SUB_ID>> boolean update(T item) {
		U path = (U) itemToPropertyGetter.apply(item);
		ICrudConnector<U, SUB_ID> connector = Connectors.getConnector(path.getItemClass());
		return connector.update(path);
	}
	
}
