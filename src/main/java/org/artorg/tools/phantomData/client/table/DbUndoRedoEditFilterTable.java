package org.artorg.tools.phantomData.client.table;

import java.util.function.Consumer;

import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.commandPattern.UndoRedoNode;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbUndoRedoEditFilterTable<ITEM extends DbPersistent<ITEM,?>> extends DbFilterTable<ITEM> implements IDbUndoRedoEditFilterTable<ITEM> {
	
//	private void setFilteredValue(ITEM item, ITEM filteredItem, int filteredCol, String value, Consumer<String> redo, Consumer<String> undo) {
//		String currentValue = getFilteredValue(item, filteredCol);
//		if (value.equals(currentValue))  return;
//		
//		UndoRedoNode node = new UndoRedoNode(() -> {
//				getFilteredColumns().get(filteredCol).set(filteredItem, value);
//				redo.accept(value);
//			}, () -> {
//				getFilteredColumns().get(filteredCol).set(filteredItem, currentValue);
//				undo.accept(currentValue);
//			}, () -> {
//				getFilteredColumns().get(filteredCol).update(filteredItem);
//		});
//		
//		
//		getUndoManager().addAndRun(node);
//	}
//	
//	public void setFilteredValue(int row, int col, String value, Consumer<String> redo, Consumer<String> undo) {
//		ITEM superItem = getItems().stream().filter(item -> item.getId().equals(getFilteredItems().get(row).getId())).findFirst().get();
//		setFilteredValue(superItem, getFilteredItems().get(row), col, value, redo, undo);
//	}
//	
//	public void setFilteredValue(ITEM item, int filteredCol, String value, Consumer<String> redo, Consumer<String> undo) {
//		ITEM superItem = getItems().stream().filter(i -> i.getId().equals(item.getId())).findFirst().get();
//		setFilteredValue(superItem, item, filteredCol, value, redo, undo);
//	}
//	
//	public void setFilteredValue(ITEM item, int filteredCol, String value) {
//		setFilteredValue(item, filteredCol, value, s -> {}, s -> {});
//	}

	
private final UndoManager undoManager;
	
	{
		undoManager = new UndoManager();
	}
	
	@Override
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
}
