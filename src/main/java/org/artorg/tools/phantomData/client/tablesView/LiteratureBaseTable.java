package org.artorg.tools.phantomData.client.tablesView;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.LiteratureBaseEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.LiteratureBaseFilterTable;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

public class LiteratureBaseTable extends DbUndoRedoAddEditControlFilterTableView<LiteratureBase> {

	{
		this.setTable(new LiteratureBaseFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<LiteratureBase> createAddEditController() {
		return new LiteratureBaseEditFactoryController(this);
	}
	

}
