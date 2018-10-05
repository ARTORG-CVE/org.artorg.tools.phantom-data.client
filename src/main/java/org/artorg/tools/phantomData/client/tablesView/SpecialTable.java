package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.SpecialEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.SpecialFilterTable;
import org.artorg.tools.phantomData.server.model.Special;

public class SpecialTable extends DbUndoRedoAddEditControlFilterTableView<Special> {

	{
		this.setTable(new SpecialFilterTable());
	}
	
//	@Override
//	public ItemEditFactoryController<Special> createAddEditController() {
//		SpecialEditFactoryController factory = new SpecialEditFactoryController();
//		factory.setTable(this);
//		return factory;
//	}

}
