package org.artorg.tools.phantomData.client.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class Column<T extends DbPersistent<T,?>> extends AbstractColumn<T> {
	private final Function<T, Object> itemToPropertyGetter;
	private final Function<Object, String> propertyToValueGetter;
	private final BiConsumer<Object, String> propertyToValueSetter;
	
	@SuppressWarnings("unchecked")
	public <SUB extends DbPersistent<SUB,?>> Column(String columnName, Function<T, SUB> itemToPropertyGetter, 
			Function<SUB, String> propertyToValueGetter, 
			BiConsumer<SUB, String> propertyToValueSetter) {
		super(columnName);
		this.itemToPropertyGetter = (Function<T, Object>) itemToPropertyGetter;
		this.propertyToValueGetter = (Function<Object, String>) propertyToValueGetter;
		this.propertyToValueSetter = (BiConsumer<Object, String>) propertyToValueSetter;
	}
	
	@Override
	public String get(T item) {
		return propertyToValueGetter.apply(itemToPropertyGetter.apply(item));
	}
	
	@Override
	public void set(T item, String value) {
		propertyToValueSetter.accept(itemToPropertyGetter.apply(item), value);
	}

	@SuppressWarnings("unchecked")
	public <U extends DbPersistent<U,SUB_ID>, SUB_ID> boolean update(T item) {
		System.out.println("updated value in database :)");
		U path = (U) itemToPropertyGetter.apply(item);
		ICrudConnector<U,SUB_ID> connector = Connectors.getConnector(path.getItemClass());
		return connector.update(path);
	}

	@Override
	public boolean isIdColumn() {
		throw new UnsupportedOperationException();
	}
	
}
