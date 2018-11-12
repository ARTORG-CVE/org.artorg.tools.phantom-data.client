package org.artorg.tools.phantomData.client.table;

import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbUndoRedoFactoryEditFilterTable<ITEM extends DbPersistent<ITEM,?>> extends DbFilterTable<ITEM> implements IDbUndoRedoEditFilterTable<ITEM,Object> {
	private final UndoManager undoManager;
	
	{
		undoManager = new UndoManager();
	}
	
	@Override
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
}
