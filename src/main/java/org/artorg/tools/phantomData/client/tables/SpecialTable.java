package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.SpecialEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoEditFilterTableView;
import org.artorg.tools.phantomData.client.tables.filterable.SpecialFilterTable;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialTable extends DbUndoRedoEditFilterTableView<Special> {

	{
		this.setTable(new SpecialFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<Special> createAddEditController() {
		return new SpecialEditFactoryController(this);
	}

}
