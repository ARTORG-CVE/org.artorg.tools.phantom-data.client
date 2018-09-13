package org.artorg.tools.phantomData.client.scene.control.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class Column<ITEM extends DatabasePersistent<?>, 
		PATH extends DatabasePersistent<SUB_ID_TYPE>, 
		SUB_ID_TYPE> extends IColumn<ITEM, SUB_ID_TYPE> {
	private final Function<ITEM, PATH> itemToPropertyGetter;
	private final Function<PATH, String> propertyToValueGetter;
	private final BiConsumer<PATH, String> propertyToValueSetter;
	private final HttpDatabaseCrud<PATH, SUB_ID_TYPE> connector;
	
	public Column(String columnName, Function<ITEM, PATH> itemToPropertyGetter, 
			Function<PATH, String> propertyToValueGetter, 
			BiConsumer<PATH, String> propertyToValueSetter,
			HttpDatabaseCrud<PATH, SUB_ID_TYPE> connector
			) {
		super(columnName);
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
		this.connector = connector;
	}
	
	@Override
	public String get(ITEM item) {
		return propertyToValueGetter.apply(itemToPropertyGetter.apply(item));
	}
	
	@Override
	public void set(ITEM item, String value) {
		propertyToValueSetter.accept(itemToPropertyGetter.apply(item), value);
	}

	@Override
	public boolean update(ITEM item) {
		System.out.println("updated value in database :)");
		return connector.update(itemToPropertyGetter.apply(item));
	}

	
	
}
