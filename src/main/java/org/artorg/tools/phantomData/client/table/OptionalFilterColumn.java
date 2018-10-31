package org.artorg.tools.phantomData.client.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class OptionalFilterColumn<T> extends AbstractFilterColumn<T> {
	private final Function<T, Optional<? extends Object>> itemToPropertyGetter;
	private final Function<Object, String> propertyToValueGetter;
	private final BiConsumer<Object, String> propertyToValueSetter;
	private final String emptyValue;

	@SuppressWarnings("unchecked")
	public <SUB extends DbPersistent<SUB,?>> OptionalFilterColumn(String columnName, 
			Function<T, Optional<SUB>> itemToPropertyGetter, 
			Function<SUB, String> propertyToValueGetter, 
			BiConsumer<SUB, String> propertyToValueSetter,
			String emtpyValue) {
		super(columnName);
		this.itemToPropertyGetter = item -> itemToPropertyGetter.apply(item);
		this.propertyToValueGetter = (Function<Object, String>) propertyToValueGetter;
		this.propertyToValueSetter = (BiConsumer<Object, String>) propertyToValueSetter;
		this.emptyValue = emtpyValue;
	}
	
	@Override
	public String get(T item) {
		Optional<? extends Object> path = itemToPropertyGetter.apply(item);
		if (path.isPresent())  {
			Object o = path.get();
			if (o == null) return emptyValue;
			return propertyToValueGetter.apply(o);
		}
		return emptyValue;
	}
	
	@Override
	public  void set(T item, String value) {
		Optional<? extends Object> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) {
			Object o = path.get();
			if (o == null) return;
			propertyToValueSetter.accept(o, value);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends DbPersistent<U,SUB_ID>, SUB_ID extends Comparable<SUB_ID>>  boolean update(T item) {
		Optional<U> optional = (Optional<U>) itemToPropertyGetter.apply(item);
		if (!optional.isPresent()) return false;
		U sub = optional.get();
		ICrudConnector<U,SUB_ID> connector = Connectors.getConnector(sub.getItemClass()); 
		return connector.update(sub);
	}

}
