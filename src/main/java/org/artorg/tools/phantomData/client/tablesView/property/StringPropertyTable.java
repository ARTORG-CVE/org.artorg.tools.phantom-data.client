package org.artorg.tools.phantomData.client.tablesView.property;

import org.artorg.tools.phantomData.client.controller.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.controllers.editFactories.property.StringPropertyEditFactoryController;
import org.artorg.tools.phantomData.client.scene.control.DbUndoRedoAddEditControlFilterTableView;
import org.artorg.tools.phantomData.client.tablesFilter.property.StringPropertyFilterTable;
import org.artorg.tools.phantomData.server.model.property.StringProperty;

public class StringPropertyTable extends DbUndoRedoAddEditControlFilterTableView<StringProperty> {

	{
		this.setTable(new StringPropertyFilterTable());
	}
	
	@Override
	public ItemEditFactoryController<StringProperty> createAddEditController() {
		return new StringPropertyEditFactoryController(this);
	}

}
