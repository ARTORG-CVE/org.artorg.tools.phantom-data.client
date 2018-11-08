package org.artorg.tools.phantomData.client.table;

import java.util.function.Consumer;

import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbUndoRedoEditFilterTable<ITEM extends DbPersistent<ITEM,?>> extends IUndoRedoTable<ITEM>, IDbEditFilterTable<ITEM> {
	
	default void setFilteredValue(int row, int col, Object value, Consumer<Object> redo, Consumer<Object> undo) {
		ITEM superItem = getItems().stream().filter(item -> item.getId().equals(getFilteredItems().get(row).getId())).findFirst().get();
		ITEM filteredItem = getFilteredItems().get(row);
		Object currentValue = getFilteredValue(superItem, col);
		if (value.equals(currentValue))  return;
		
		UndoRedoNode node = new UndoRedoNode(() -> {
				getFilteredColumns().get(col).set(filteredItem, value);
				redo.accept(value);
			}, () -> {
				getFilteredColumns().get(col).set(filteredItem, currentValue);
				undo.accept(currentValue);
			}, () -> {
				getFilteredColumns().get(col).update(filteredItem);
		});
		
		getUndoManager().addAndRun(node);
		
	}
	
	default void setFilteredValue(ITEM item, int filteredCol, Object value, Consumer<Object> redo, Consumer<Object> undo) {
		ITEM superItem = getItems().stream().filter(i -> i.getId().equals(item.getId())).findFirst().get();
		Object currentValue = getFilteredValue(superItem, filteredCol);
		if (value.equals(currentValue))  return;
		
		UndoRedoNode node = new UndoRedoNode(() -> {
				getFilteredColumns().get(filteredCol).set(item, value);
				redo.accept(value);
			}, () -> {
				getFilteredColumns().get(filteredCol).set(item, currentValue);
				undo.accept(currentValue);
			}, () -> {
				getFilteredColumns().get(filteredCol).update(item);
		});
		
		
		getUndoManager().addAndRun(node);
	}
	
	default void setFilteredValue(ITEM item, int filteredCol, Object value) {
		setFilteredValue(item, filteredCol, value, s -> {}, s -> {});
	}
	
	
}
