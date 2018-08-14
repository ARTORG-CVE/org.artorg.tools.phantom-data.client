package org.artorg.tools.phantomData.client.commandPattern;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyUndoable<ITEM extends 
		DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE, U> {
	private final Function<ITEM,U> getter;
	private final BiConsumer<ITEM,U> setter;
	
	public PropertyUndoable(BiConsumer<ITEM,U> setter, Function<ITEM,U> getter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	public PropertyUndoable(Function<ITEM,U> getter, BiConsumer<ITEM,U> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	public boolean set(ITEM item, U value) {
		U currentValue = getter.apply(item);
		UndoRedoNode node = new UndoRedoNode(() -> setter.accept(item, value), 
				() -> setter.accept(item, currentValue),
				() -> {connector.update(item); System.out.println("   --in item block--  ");
				
					System.out.println("   /--" +item.toString());
				});
		undoManager.addAndRun(node);
		return true;
	}
	
	public U get(ITEM item) {
		return getter.apply(item);
	}

	public Function<ITEM, U> getGetter() {
		return getter;
	}

	public BiConsumer<ITEM, U> getSetter() {
		return setter;
	}
	
}
