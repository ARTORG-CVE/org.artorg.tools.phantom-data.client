package org.artorg.tools.phantomData.client.commandPattern;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyUndoable<T,U> extends UndoManager {

	private final Function<T,U> getter;
	
	

	private final BiConsumer<T,U> setter;
			
	public PropertyUndoable(BiConsumer<T,U> setter, Function<T,U> getter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	public void set(T item, U value) {
		U currentValue = getter.apply(item);
		UndoRedoNode node = new UndoRedoNode(() -> setter.accept(item, value), 
				() -> setter.accept(item, currentValue));
		super.addNode(node);
		node.redo();
	}
	
	public U get(T item) {
		return getter.apply(item);
	}

	public Function<T, U> getGetter() {
		return getter;
	}

	public BiConsumer<T, U> getSetter() {
		return setter;
	}
}
