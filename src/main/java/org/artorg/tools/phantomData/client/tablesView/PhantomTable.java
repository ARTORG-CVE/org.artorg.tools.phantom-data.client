package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.LiteratureBaseEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.PhantomEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.PhantomFilterTable;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomTable extends DbUndoRedoAddEditControlFilterTableView<Phantom> {

	{
		PhantomFilterTable table = new PhantomFilterTable();
		this.setTable(table);
	}
	
//	@Override
//	public ItemEditFactoryController<Phantom> createAddEditController() {
//		PhantomEditFactoryController factory = new PhantomEditFactoryController();
//		factory.setTable(this);
//		return factory;
//	}

}
