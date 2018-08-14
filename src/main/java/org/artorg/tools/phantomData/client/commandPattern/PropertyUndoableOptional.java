package org.artorg.tools.phantomData.client.commandPattern;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyUndoableOptional <ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE, U> {
	private final Function<Optional<ITEM>,U> getter;
	private final BiConsumer<Optional<ITEM>,U> setter;
//	private final HttpDatabaseCrud<ITEM, ID_TYPE> connector;
//	private final UndoManager undoManager;
	
//	public PropertyUndoableOptional(BiConsumer<ITEM,U> setter, Function<ITEM,U> getter
////			, HttpDatabaseCrud<ITEM, ID_TYPE> connector, UndoManager undoManager
//			) {
//		this.getter = getter;
//		this.setter = setter;
////		this.connector = connector;
////		this.undoManager = undoManager;
//	}
	
	public PropertyUndoableOptional(Function<Optional<ITEM>,U> getter, BiConsumer<Optional<ITEM>,U> setter 
//			, HttpDatabaseCrud<ITEM, ID_TYPE> connector, UndoManager undoManager
			) {
		
		this.getter = getter;
//		this.getter = getter.compose((ITEM item) -> Optional.of(item));
//		this.setter = (ITEM item, U value) -> setter.accept(Optional.of(item), value);
		this.setter = setter;
		
		
//		this.getter = getter;
//		this.setter = setter;
//		this.connector = connector;
//		this.undoManager = undoManager;
	}



//	public void set(Optional<ITEM> item, U value) {
//		
//		Optional<U> currentValue = getter.apply(item);
//		if (currentValue.isPresent()) {
//		UndoRedoNode node = new UndoRedoNode(() -> setter.accept(item, Optional.of(value)), 
//				() -> setter.accept(item, currentValue),
//				() -> {connector.update(item); System.out.println("   --in item block--  ");
//				
//					System.out.println("   /--" +item.toString());
//				});
//		undoManager.addAndRun(node);
//		}
//	}
//	
	public U get(Optional<ITEM> item) {
			return getter.apply(item);
	}

//	public Function<ITEM, U> getGetter() {
//		return getter;
//	}
//
//	public BiConsumer<ITEM, U> getSetter() {
//		return setter;
//	}
	
//	public UndoManager getUndoManager() {
//		return undoManager;
//	}
//	
}
