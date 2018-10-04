package org.artorg.tools.phantomData.client.tablesView.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.IntegerPropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.property.IntegerPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.IntegerProperty;

public class IntegerPropertyTable extends DbUndoRedoAddEditControlFilterTableView<IntegerProperty> {

	{
		this.setTable(new IntegerPropertyFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<IntegerProperty> createAddEditController() {
		return new IntegerPropertyEditFactoryController(this);
	}

}
