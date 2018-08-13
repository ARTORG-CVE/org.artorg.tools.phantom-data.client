package org.artorg.tools.phantomData.client.commandPattern;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.artorg.tools.phantomData.client.specification.HttpDatabaseCrud;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

public class PropertyUndoable<ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE, U> extends UndoManager {

	private final Function<ITEM,U> getter;
	
	

	private final BiConsumer<ITEM,U> setter;
			
	private final HttpDatabaseCrud<ITEM, ID_TYPE> connector;
	
	
	public PropertyUndoable(BiConsumer<ITEM,U> setter, Function<ITEM,U> getter, HttpDatabaseCrud<ITEM, ID_TYPE> connector) {
		this.getter = getter;
		this.setter = setter;
		this.connector = connector;
	}
	
	public void set(ITEM item, U value) {
		U currentValue = getter.apply(item);
		UndoRedoNode node = new UndoRedoNode(() -> setter.accept(item, value), 
				() -> setter.accept(item, currentValue),
				() -> connector.update(item));
		super.addNode(node);
		node.redo();
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
