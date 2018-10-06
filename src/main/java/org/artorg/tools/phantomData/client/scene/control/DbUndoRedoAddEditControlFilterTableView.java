package org.artorg.tools.phantomData.client.scene.control;

import org.artorg.tools.phantomData.server.specification.DbPersistent;

public class DbUndoRedoAddEditControlFilterTableView<ITEM extends DbPersistent<ITEM,?>> extends DbEditFilterTableView<ITEM> {

	public DbUndoRedoAddEditControlFilterTableView() {}
	
	public DbUndoRedoAddEditControlFilterTableView(Class<ITEM> itemClass) {
		super(itemClass);
	}
	
}
