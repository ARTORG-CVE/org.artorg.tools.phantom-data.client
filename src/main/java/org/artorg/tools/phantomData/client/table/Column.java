package org.artorg.tools.phantomData.client.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class Column<ITEM extends DatabasePersistent<ITEM, ?>, 
		PATH extends DatabasePersistent<PATH, SUB_ID_TYPE>, 
		SUB_ID_TYPE> implements IColumn<ITEM, SUB_ID_TYPE> {
	private final String columnName;
	private final Function<ITEM, PATH> itemToPropertyGetter;
	private final Function<PATH, String> propertyToValueGetter;
	private final BiConsumer<PATH, String> propertyToValueSetter;
	private final HttpDatabaseCrud<PATH, SUB_ID_TYPE> connector;
	
	public Column(String columnName, Function<ITEM, PATH> itemToPropertyGetter, 
			Function<PATH, String> propertyToValueGetter, 
			BiConsumer<PATH, String> propertyToValueSetter,
			HttpDatabaseCrud<PATH, SUB_ID_TYPE> connector
			) {
		this.columnName = columnName;
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
	public String getColumnName() {
		return columnName;
	}

	@Override
	public boolean update(ITEM item) {
		System.out.println("updated value in database :)");
		return connector.update(itemToPropertyGetter.apply(item));
	}
	
}
