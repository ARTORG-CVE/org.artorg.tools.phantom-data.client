package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class ColumnOptional<ITEM extends DbPersistent, 
		PATH extends DbPersistent> extends IColumn<ITEM> {
	private final Function<ITEM, Optional<PATH>> itemToPropertyGetter;
	private final Function<PATH, String> propertyToValueGetter;
	private final BiConsumer<PATH, String> propertyToValueSetter;
	private final String emptyValue;
	private final HttpConnectorSpring<PATH> connector;
	
	public ColumnOptional( String columnName,
			Function<ITEM, Optional<PATH>> itemToPropertyGetter, 
			Function<PATH, String> propertyToValueGetter, 
			BiConsumer<PATH, String> propertyToValueSetter,
			String emptyValue,
			HttpConnectorSpring<PATH> connector
			) {
		super(columnName);
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
		this.emptyValue = emptyValue;
		this.connector = connector;
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

	@Override
	public boolean update(ITEM item) {
		System.out.println("updated value in database :)");
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) 
			return this.connector.update(path.get());
		return false;
	}

}