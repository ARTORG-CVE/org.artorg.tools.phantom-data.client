package org.artorg.tools.phantomData.client.table;

import java.util.function.Consumer;

import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;

public interface IUndoRedoTable<ITEM> extends IEditTable<ITEM> {
	
	UndoManager getUndoManager();
	
	default void setValue(ITEM item, int col, Object value, Consumer<Object> redo, Consumer<Object> undo) {
		Object currentValue = getValue(item, col);
		if (value.equals(currentValue))  return;
		
		UndoRedoNode node = new UndoRedoNode(() -> {
				getColumns().get(col).set(item, value);
				redo.accept(value);
			}, () -> {
				getColumns().get(col).set(item, currentValue);
				undo.accept(currentValue);
			}, () -> {
				getColumns().get(col).update(item);
				});
		getUndoManager().addAndRun(node);
	}
	
	default void setValue(int row, int col, Object value, Consumer<Object> redo, Consumer<Object> undo) {
		setValue(getItems().get(row), col, value, redo, undo);
	}

}
