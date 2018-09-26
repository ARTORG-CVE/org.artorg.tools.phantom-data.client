package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.PhantomEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.PhantomFilterTable;
import org.artorg.tools.phantomData.server.model.Phantom;

public class PhantomTable extends TableViewSpring<Phantom> {

	{
		this.setTable(new PhantomFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<Phantom> createAddEditController() {
		return new PhantomEditFactoryController(this);
	}

}
