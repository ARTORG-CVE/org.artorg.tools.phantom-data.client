package org.artorg.tools.phantomData.client.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class ColumnOptional<ITEM extends DbPersistent<ITEM>, 
		PATH extends DbPersistent<PATH>> extends IColumn<ITEM> {
	private final Function<ITEM, Optional<PATH>> itemToPropertyGetter;
	private final Function<PATH, String> propertyToValueGetter;
	private final BiConsumer<PATH, String> propertyToValueSetter;
	private final String emptyValue;
	
	public ColumnOptional( String columnName,
			Function<ITEM, Optional<PATH>> itemToPropertyGetter, 
			Function<PATH, String> propertyToValueGetter, 
			BiConsumer<PATH, String> propertyToValueSetter,
			String emptyValue
			) {
		super(columnName);
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
		this.emptyValue = emptyValue;
	}
	
	@Override
	public String get(ITEM item) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) 
			return propertyToValueGetter.apply(path.get());
		return emptyValue;
	}
	
	@Override
	public  void set(ITEM item, String value) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent())
			propertyToValueSetter.accept(path.get(), value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean update(ITEM item) {
		System.out.println("updated value in database :)");
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		HttpConnectorSpring<PATH> connector = Connectors.getConnector(item.getClass());
		if (path.isPresent()) 
			return connector.update(path.get());
		return false;
	}

}