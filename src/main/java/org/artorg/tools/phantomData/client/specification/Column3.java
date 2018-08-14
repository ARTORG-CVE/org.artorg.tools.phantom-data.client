package org.artorg.tools.phantomData.client.specification;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class Column3<ITEM extends DatabasePersistent<ITEM, ?>, PATH extends DatabasePersistent<PATH, SUB_ID_TYPE>, CELL_TYPE, SUB_ID_TYPE> {
//	private Function<ITEM,CELL_TYPE> getter;
//	private BiConsumer<ITEM,CELL_TYPE> setter;
	private final String columnName;
	private final Function<ITEM, Optional<PATH>> itemToPropertyGetter;
//	private Function<PATH, CELL_TYPE> propertyToValueGetter;
//	private BiConsumer<PATH, CELL_TYPE> propertyToValueSetter;
	private final PropertyUndoable<PATH,SUB_ID_TYPE, CELL_TYPE> property;
	
	public Column3( Function<ITEM, Optional<PATH>> itemToPropertyGetter, 
			Function<Optional<PATH>, CELL_TYPE> propertyToValueGetter, 
			BiConsumer<Optional<PATH>, CELL_TYPE> propertyToValueSetter,
			String columnName
//			,
//			HttpDatabaseCrud<PATH, Integer> connector,
//			UndoManager undoManager
			) {
		this.columnName = columnName;
		this.itemToPropertyGetter = itemToPropertyGetter;
//		this.propertyToValueGetter = propertyToValueGetter;
//		this.propertyToValueSetter = propertyToValueSetter;
		
//		this.getter = itemToPropertyGetter.andThen(propertyToValueGetter);		
//		this.setter = (item, o) -> propertyToValueSetter.accept(itemToPropertyGetter.apply(item),o); 
		
		
		
		property = new PropertyUndoable<PATH, SUB_ID_TYPE, CELL_TYPE>(propertyToValueGetter, propertyToValueSetter 
//				, connector, undoManager
				);
		
	}
	
	public Column3(String columnName, Function<ITEM, PATH> itemToPropertyGetter, 
			Function<PATH, CELL_TYPE> propertyToValueGetter, 
			BiConsumer<PATH, CELL_TYPE> propertyToValueSetter
//			,
//			HttpDatabaseCrud<PATH, Integer> connector,
//			UndoManager undoManager
			) {
		this.columnName = columnName;
		
		
		
		this.itemToPropertyGetter = itemToPropertyGetter.compose((Optional<ITEM> opt) -> opt.get());
//		this.propertyToValueGetter = propertyToValueGetter;
//		this.propertyToValueSetter = propertyToValueSetter;
		
//		this.getter = itemToPropertyGetter.andThen(propertyToValueGetter);		
//		this.setter = (item, o) -> propertyToValueSetter.accept(itemToPropertyGetter.apply(item),o); 
		
		property = new PropertyUndoable<PATH, SUB_ID_TYPE, CELL_TYPE>(propertyToValueSetter, propertyToValueGetter
//				, connector, undoManager
				);
		
	}
	
	public final Optional<CELL_TYPE> get(ITEM item) {
		return property.get(itemToPropertyGetter.apply(item));
	}
	
	public final void set(ITEM item, CELL_TYPE value) {
		property.set(itemToPropertyGetter.apply(Optional.of(item)), value);
	}
	

}
