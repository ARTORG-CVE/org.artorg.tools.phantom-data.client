package org.artorg.tools.phantomData.client.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class ColumnOptional<ITEM extends DatabasePersistent<ITEM, ?>, 
		PATH extends DatabasePersistent<PATH, SUB_ID_TYPE>, 
		CELL_TYPE, SUB_ID_TYPE> implements IColumn<ITEM, CELL_TYPE, SUB_ID_TYPE> {
	private final String columnName;
	private final Function<ITEM, Optional<PATH>> itemToPropertyGetter;
	

	private final Function<PATH, CELL_TYPE> propertyToValueGetter;
	private final BiConsumer<PATH, Object> propertyToValueSetter;
	private final CELL_TYPE emptyValue;
	private final HttpDatabaseCrud<PATH, SUB_ID_TYPE> connector;
	
	public ColumnOptional( String columnName,
			Function<ITEM, Optional<PATH>> itemToPropertyGetter, 
			Function<PATH, CELL_TYPE> propertyToValueGetter, 
			BiConsumer<PATH, Object> propertyToValueSetter,
			CELL_TYPE emptyValue,
			HttpDatabaseCrud<PATH, SUB_ID_TYPE> connector
			) {
		this.columnName = columnName;
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.propertyToValueGetter = propertyToValueGetter;
		this.propertyToValueSetter = propertyToValueSetter;
		this.emptyValue = emptyValue;
		this.connector = connector;
	}
	
	@Override
	public CELL_TYPE get(ITEM item) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) 
			return propertyToValueGetter.apply(path.get());
		return emptyValue;
	}
	
	@Override
	public  void set(ITEM item, Object value) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent())
			propertyToValueSetter.accept(path.get(), value);
	}

	@Override
	public String getColumnName() {
		return columnName;
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