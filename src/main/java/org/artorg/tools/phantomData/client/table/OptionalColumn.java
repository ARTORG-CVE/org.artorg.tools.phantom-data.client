package org.artorg.tools.phantomData.client.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class OptionalColumn<T extends DbPersistent<T,?>> extends AbstractColumn<T> {
	private final Function<T, Optional<? extends Object>> itemToPropertyGetter;
	private final Function<Object, String> propertyToValueGetter;
	private final BiConsumer<Object, String> propertyToValueSetter;
	private final String emptyValue;
	
	@SuppressWarnings("unchecked")
	public <SUB extends DbPersistent<SUB,?>> OptionalColumn( String columnName,
			Function<T, Optional<SUB>> itemToPropertyGetter, 
			Function<SUB, String> propertyToValueGetter, 
			BiConsumer<SUB, String> propertyToValueSetter,
			String emptyValue
			) {
		super(columnName);
		this.itemToPropertyGetter = item -> itemToPropertyGetter.apply(item);
		this.propertyToValueGetter = sub -> propertyToValueGetter.apply((SUB)sub);
		this.propertyToValueSetter = (sub,value) -> propertyToValueSetter.accept(((SUB)sub), value);
		this.emptyValue = emptyValue;
	}
	
	@Override
	public String get(T item) {
		Optional<? extends Object> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) 
			return propertyToValueGetter.apply(path.get());
		return emptyValue;
	}
	
	@Override
	public  void set(T item, String value) {
		Optional<? extends Object> path = itemToPropertyGetter.apply(item);
		if (path.isPresent())
			propertyToValueSetter.accept(path.get(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U extends DbPersistent<U,SUB_ID>, SUB_ID extends Comparable<SUB_ID>>  boolean update(T item) {
		Optional<U> optional = (Optional<U>) itemToPropertyGetter.apply(item);
		if (!optional.isPresent()) return false;
		U sub = optional.get();
		ICrudConnector<U,SUB_ID> connector = Connectors.getConnector(sub.getItemClass()); 
		return connector.update(sub);
	}

}