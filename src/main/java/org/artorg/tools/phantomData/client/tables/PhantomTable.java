package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.PhantomEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbEditFilterTableView;
import org.artorg.tools.phantomData.client.tables.filterable.PhantomFilterTable;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomTable extends DbEditFilterTableView<Phantom> {

	{
		this.setTable(new PhantomFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<Phantom> createAddEditController() {
		return new PhantomEditFactoryController(this);
	}

}
