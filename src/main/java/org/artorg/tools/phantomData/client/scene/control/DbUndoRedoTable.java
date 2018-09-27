package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.commandPattern.UndoManager;
import org.artorg.tools.phantomData.client.table.UndoRedoableTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbUndoRedoTable<ITEM extends DbPersistent<ITEM,ID>, ID> extends DbTable<ITEM,ID> implements UndoRedoableTable<ITEM> {
	protected final UndoManager undoManager;
	
	{
		undoManager = new UndoManager();
	}

	@Override
	public UndoManager getUndoManager() {
		return this.undoManager;
	}

}
