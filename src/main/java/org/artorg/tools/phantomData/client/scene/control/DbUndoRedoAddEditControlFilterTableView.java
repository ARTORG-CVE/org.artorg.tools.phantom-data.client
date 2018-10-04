package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.client.table.DbUndoRedoEditFilterTable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

public abstract class DbUndoRedoAddEditControlFilterTableView<ITEM extends DbPersistent<ITEM,?>> extends DbEditFilterTableView<ITEM, DbUndoRedoEditFilterTable<ITEM>> {
	
}
