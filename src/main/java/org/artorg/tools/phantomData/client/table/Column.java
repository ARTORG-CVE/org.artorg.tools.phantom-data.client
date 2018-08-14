package org.artorg.tools.phantomData.client.table;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class Column<ITEM extends DatabasePersistent<ITEM, ?>, 
		PATH extends DatabasePersistent<PATH, SUB_ID_TYPE>, 
		CELL_TYPE, SUB_ID_TYPE> implements IColumn<ITEM, CELL_TYPE, SUB_ID_TYPE> {
	private final String columnName;
	private final Function<ITEM, PATH> itemToPropertyGetter;
	private final PropertyUndoable<PATH,SUB_ID_TYPE, CELL_TYPE> property;
	
	public Column(String columnName, Function<ITEM, PATH> itemToPropertyGetter, 
			Function<PATH, CELL_TYPE> propertyToValueGetter, 
			BiConsumer<PATH, CELL_TYPE> propertyToValueSetter
			) {
		this.columnName = columnName;
		this.itemToPropertyGetter = itemToPropertyGetter;
		this.property = new PropertyUndoable<PATH, SUB_ID_TYPE, CELL_TYPE>(
				propertyToValueSetter, propertyToValueGetter);
	}
	
	public final CELL_TYPE get(ITEM item) {
		return property.get(itemToPropertyGetter.apply(item));
	}
	
	public final boolean set(ITEM item, CELL_TYPE value) {
		return property.set(itemToPropertyGetter.apply(item), value);
	}

	public String getColumnName() {
		return columnName;
	}
	
}
