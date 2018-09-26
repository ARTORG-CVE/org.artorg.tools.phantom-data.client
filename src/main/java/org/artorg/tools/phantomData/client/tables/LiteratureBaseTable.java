package org.artorg.tools.phantomData.client.tables;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.LiteratureBaseEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.TableViewSpring;
import org.artorg.tools.phantomData.client.tables.filterable.LiteratureBaseFilterTable;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseTable extends TableViewSpring<LiteratureBase> {

	{
		this.setTable(new LiteratureBaseFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<LiteratureBase> createAddEditController() {
		return new LiteratureBaseEditFactoryController(this);
	}
	

}
