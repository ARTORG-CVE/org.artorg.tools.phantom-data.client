package org.artorg.tools.phantomData.client.specification;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoable;
import org.artorg.tools.phantomData.client.commandPattern.PropertyUndoableOptional;
import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class Column2<ITEM extends DatabasePersistent<ITEM, ?>, PATH extends DatabasePersistent<PATH, SUB_ID_TYPE>, CELL_TYPE, SUB_ID_TYPE> {
//	private Function<ITEM,CELL_TYPE> getter;
//	private BiConsumer<ITEM,CELL_TYPE> setter;
	private final String columnName;
	private final Function<ITEM, Optional<PATH>> itemToPropertyGetter;
//	private Function<PATH, CELL_TYPE> propertyToValueGetter;
//	private BiConsumer<PATH, CELL_TYPE> propertyToValueSetter;
	private final PropertyUndoable<PATH,SUB_ID_TYPE, CELL_TYPE> property;
	private final CELL_TYPE emptyValue;
	
	public Column2( String columnName,
			Function<ITEM, Optional<PATH>> itemToPropertyGetter, 
			Function<PATH, CELL_TYPE> propertyToValueGetter, 
			BiConsumer<PATH, CELL_TYPE> propertyToValueSetter,
			CELL_TYPE emptyValue
//			,
//			HttpDatabaseCrud<PATH, Integer> connector,
//			UndoManager undoManager
			) {
		this.columnName = columnName;
		this.itemToPropertyGetter = itemToPropertyGetter;
//		Function<Optional<PATH>, CELL_TYPE> propertyToValueGetterOptional; 
//		BiConsumer<Optional<PATH>, CELL_TYPE> propertyToValueSetterOptional;
		this.emptyValue = emptyValue;
		
//		propertyToValueGetterOptional =  
		
//		this.propertyToValueGetter = propertyToValueGetter;
//		this.propertyToValueSetter = propertyToValueSetter;
		
//		this.getter = itemToPropertyGetter.andThen(propertyToValueGetter);		
		
//		this.getter = itemToPropertyGetter.andThen(opt -> {
//			if (opt.isPresent()) return propertyToValueGetter.apply(opt.get());
//			return emptyValue;
//		});
		
//		this.setter = (item, o) -> propertyToValueSetter.accept(itemToPropertyGetter.apply(item),o); 
		
		
		
		property = new PropertyUndoable<PATH, SUB_ID_TYPE, CELL_TYPE>(propertyToValueGetter, propertyToValueSetter 
//				, connector, undoManager
				);
		
	}
	
	public final CELL_TYPE get(ITEM item) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) return property.get(path.get());
		return emptyValue;
	}
	
	public final boolean set(ITEM item, CELL_TYPE value) {
		Optional<PATH> path = itemToPropertyGetter.apply(item);
		if (path.isPresent()) {
			property.set(path.get(), value);
			return true;
		}
		return false;
	}
	

}