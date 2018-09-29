package org.artorg.tools.phantomData.client.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.CrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class ColumnOptional<T extends DbPersistent<T,?>, 
SUB extends DbPersistent<SUB,?>> extends IColumn<T> {
	private final Function<T, Optional<SUB>> itemToPropertyGetter;
	private final Function<SUB, String> propertyToValueGetter;
	private final BiConsumer<SUB, String> propertyToValueSetter;
	private final String emptyValue;
	
	public ColumnOptional( String columnName,
			Function<T, Optional<SUB>> itemToPropertyGetter, 
			Function<SUB, String> propertyToValueGetter, 
			BiConsumer<SUB, String> propertyToValueSetter,
			String emptyValue
			) {
		super(columnName);
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
		this.emptyValue = emptyValue;
	}
	
	@Override
	public String get(T item) {
		Optional<SUB> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) 
			return propertyToValueGetter.apply(path.get());
		return emptyValue;
	}
	
	@Override
	public  void set(T item, String value) {
		Optional<SUB> path = itemToPropertyGetter.apply(item);
		if (path.isPresent())
			propertyToValueSetter.accept(path.get(), value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends DbPersistent<U,SUB_ID>, SUB_ID>  boolean update(T item) {
		System.out.println("updated value in database :)");
		Optional<U> optional = (Optional<U>) itemToPropertyGetter.apply(item);
		if (!optional.isPresent()) return false;
		U sub = optional.get();
		CrudConnector<U,SUB_ID> connector = Connectors.getConnector(sub.getItemClass()); 
		return connector.update(sub);
	}
	
	@Override
	public boolean isIdColumn() {
		throw new UnsupportedOperationException();
	}

}