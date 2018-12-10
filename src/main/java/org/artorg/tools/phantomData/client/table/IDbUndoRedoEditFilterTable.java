package org.artorg.tools.phantomData.client.table;

import java.util.function.Consumer;

import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public interface IDbUndoRedoEditFilterTable<ITEM extends DbPersistent<ITEM,?>,R> extends IUndoRedoTable<ITEM,R>, IDbEditFilterTable<ITEM,R> {
	
	default void setFilteredValue(int row, int col, R value, Consumer<R> redo, Consumer<R> undo) {
		ITEM superItem = getItems().stream().filter(item -> item.getId().equals(getFilteredItems().get(row).getId())).findFirst().get();
		ITEM filteredItem = getFilteredItems().get(row);
		R currentValue = getFilteredValue(superItem, col);
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
	
	default void setFilteredValue(ITEM item, int filteredCol, R value, Consumer<R> redo, Consumer<R> undo) {
		ITEM superItem = getItems().stream().filter(i -> i.getId().equals(item.getId())).findFirst().get();
		R currentValue = getFilteredValue(superItem, filteredCol);
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
	
	default void setFilteredValue(ITEM item, int filteredCol, R value) {
		setFilteredValue(item, filteredCol, value, s -> {}, s -> {});
	}
	
	
}
