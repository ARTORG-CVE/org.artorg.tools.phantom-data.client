package org.artorg.tools.phantomData.client.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class OptionalFilterColumn<T,R> extends AbstractFilterColumn<T,R> {
	private final Function<T, Optional<? extends Object>> itemToPropertyGetter;
	private final Function<Object, R> propertyToValueGetter;
	private final BiConsumer<Object, R> propertyToValueSetter;
	private final R emptyValue;

	@SuppressWarnings("unchecked")
	public <SUB extends DbPersistent<SUB,?>> OptionalFilterColumn(String columnName, 
			Function<T, Optional<SUB>> itemToPropertyGetter, 
			Function<SUB, R> propertyToValueGetter, 
			BiConsumer<SUB, R> propertyToValueSetter,
			R emtpyValue) {
		super(columnName);
		this.itemToPropertyGetter = item -> itemToPropertyGetter.apply(item);
		this.propertyToValueGetter = (Function<Object, R>) propertyToValueGetter;
		this.propertyToValueSetter = (BiConsumer<Object, R>) propertyToValueSetter;
		this.emptyValue = emtpyValue;
	}
	
	@Override
	public R get(T item) {
		Optional<? extends Object> path = itemToPropertyGetter.apply(item);
		if (path.isPresent())  {
			Object o = path.get();
			if (o == null) return emptyValue;
			return propertyToValueGetter.apply(o);
		}
		return emptyValue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public  void set(T item, Object value) {
		Optional<? extends Object> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) {
			Object o = path.get();
			if (o == null) return;
			propertyToValueSetter.accept(o, (R)value);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends DbPersistent<U,?>>  boolean update(T item) {
		Optional<U> optional = (Optional<U>) itemToPropertyGetter.apply(item);
		if (!optional.isPresent()) return false;
		U sub = optional.get();
		ICrudConnector<U,?> connector = Connectors.getConnector(sub.getItemClass()); 
		return connector.update(sub);
	}

}
