package org.artorg.tools.phantomData.client.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class FilterColumn<T> extends AbstractFilterColumn<T> {
	private final Function<T, Object> itemToPropertyGetter;
	private final Function<Object, String> propertyToValueGetter;
	private final BiConsumer<Object, String> propertyToValueSetter;

	@SuppressWarnings("unchecked")
	public <SUB extends DbPersistent<SUB,?>> FilterColumn(String columnName, Function<T, SUB> itemToPropertyGetter, 
			Function<SUB, String> propertyToValueGetter, 
			BiConsumer<SUB, String> propertyToValueSetter) {
		super(columnName);
		this.itemToPropertyGetter = (Function<T, Object>) itemToPropertyGetter;
		this.propertyToValueGetter = (Function<Object, String>) propertyToValueGetter;
		this.propertyToValueSetter = (BiConsumer<Object, String>) propertyToValueSetter;
	}
	
	@Override
	public String get(T item) {
		Object o = itemToPropertyGetter.apply(item);
		if (o == null) return "";
		return propertyToValueGetter.apply(o);
	}

	@Override
	public void set(T item, String value) {
		Object o = itemToPropertyGetter.apply(item);
		if (o == null) return;
		propertyToValueSetter.accept(o, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U extends DbPersistent<U, SUB_ID>, SUB_ID extends Comparable<SUB_ID>> boolean update(T item) {
		U path = (U) itemToPropertyGetter.apply(item);
		ICrudConnector<U, SUB_ID> connector = Connectors.getConnector(path.getItemClass());
		return connector.update(path);
	}
	
}
