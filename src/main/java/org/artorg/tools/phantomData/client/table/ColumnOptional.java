package org.artorg.tools.phantomData.client.table;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class ColumnOptional<ITEM extends DatabasePersistent<ITEM, ?>, 
		PATH extends DatabasePersistent<PATH, SUB_ID_TYPE>, 
		CELL_TYPE, SUB_ID_TYPE> implements IColumn<ITEM, CELL_TYPE, SUB_ID_TYPE> {
	private final String columnName;
	private final Function<ITEM, Optional<PATH>> itemToPropertyGetter;
	private final PropertyUndoable<PATH,SUB_ID_TYPE, CELL_TYPE> property;
	private final CELL_TYPE emptyValue;
	
	public ColumnOptional( String columnName,
			Function<ITEM, Optional<PATH>> itemToPropertyGetter, 
			Function<PATH, CELL_TYPE> propertyToValueGetter, 
			BiConsumer<PATH, CELL_TYPE> propertyToValueSetter,
			CELL_TYPE emptyValue
			) {
		this.columnName = columnName;
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.emptyValue = emptyValue;
		this.property = new PropertyUndoable<PATH, SUB_ID_TYPE, CELL_TYPE>(
				propertyToValueGetter, propertyToValueSetter);
	}
	
	public final CELL_TYPE get(ITEM item) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) return property.get(path.get());
		return emptyValue;
	}
	
	public final boolean set(ITEM item, CELL_TYPE value) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent())
			return property.set(path.get(), value);
		return false;
	}

	public String getColumnName() {
		return columnName;
	}

}