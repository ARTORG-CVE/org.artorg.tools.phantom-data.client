package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.PhantomEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.client.tables.filterable.PhantomFilterTable;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomTable extends DbUndoRedoEditFilterTableView<Phantom> {

	{
		PhantomFilterTable table = new PhantomFilterTable();
		this.setTable(table);
	}
	
	@Override
	public ItemEditFactoryController<Phantom> createAddEditController() {
		return new PhantomEditFactoryController(this);
	}

}
