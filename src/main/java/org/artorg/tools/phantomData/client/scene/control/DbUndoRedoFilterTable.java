package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.table.IUndoRedoTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbUndoRedoFilterTable<ITEM extends DbPersistent<ITEM,?>> extends DbFilterTable<ITEM> implements IUndoRedoTable<ITEM> {
	private final UndoManager undoManager;
	
	{
		undoManager = new UndoManager();
	}
	
	@Override
	public UndoManager getUndoManager() {
		return undoManager;
	}

}
